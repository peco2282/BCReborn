/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.api.blueprints.BuilderAPI;
import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.core.CoreConfig;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.builders.BuildersBlock;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.block.FrameBlock;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.builder.AbstractBuilderBlockEntity;
import com.peco2282.bcreborn.common.internal.IBoxProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.utils.BlockMiner;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;

import java.util.*;

/**
 * Port of the original {@code buildcraft.builders.TileQuarry}.
 * <p>
 * Stages: BUILDING (frame) -> IDLE (pick the next column) -> MOVING (head travels) -> DIGGING (BlockMiner)
 * -> IDLE ... -> DONE. Accepts the Off / On / Loop control modes from gates.
 */
public class QuarryBlockEntity extends AbstractBuilderBlockEntity implements IBoxProvider, IControllable {
  private final SimpleInventory inv = new SimpleInventory(18, "Quarry", 64);
  private final Deque<int[]> visitList = new LinkedList<>();
  private final Deque<BlockPos> frameList = new LinkedList<>();
  public Box box = new Box();
  private Stage stage = Stage.BUILDING;
  private int targetX, targetY, targetZ;
  private double headPosX, headPosY, headPosZ;
  private double prevHeadPosX, prevHeadPosY, prevHeadPosZ;
  private float headTrajectory;
  private double headSpeed;
  private boolean restoredHead;
  private boolean movingHorizontally, movingVertically;
  private BlockMiner miner;
  /** Breaks a block that sits where a frame has to go (the original blueprint builder cleared it). */
  @Nullable
  private BlockMiner frameMiner;
  @Nullable
  private UUID placedBy;

  public QuarryBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.QUARRY.get(), pos, state);
    box.kind = Box.Kind.STRIPES;
    // Original: new RFBattery(2 * 64 * BREAK_ENERGY * miningMultiplier, 1000 * miningMultiplier, 0)
    double multiplier = CoreConfig.getMiningUsageMultiplier();
    setBattery(new EnergyStorage((int) (2 * 64 * BuilderAPI.BREAK_ENERGY * multiplier), (int) (1000 * multiplier), 0));
  }

  @Override
  public void initialize() {
    if (!getLevel().isClientSide) {
      if (!box.isInitialized() || box.xMax - box.xMin < 2 || box.zMax - box.zMin < 2) {
        setBoundaries();
      }
      if (!restoredHead) {
        headPosX = box.xMin + 1.5;
        headPosY = worldPosition.getY() + 2.0;
        headPosZ = box.zMin + 1.5;
      }
      prevHeadPosX = headPosX;
      prevHeadPosY = headPosY;
      prevHeadPosZ = headPosZ;
    } else {
      if (box.isInitialized()) {
        box.createLaserData();
      }
    }
  }

  @Override
  public Box getBox() {
    return box;
  }

  public double getHeadPosX(float partialTicks) {
    return prevHeadPosX + (headPosX - prevHeadPosX) * partialTicks;
  }

  public double getHeadPosY(float partialTicks) {
    return prevHeadPosY + (headPosY - prevHeadPosY) * partialTicks;
  }

  public double getHeadPosZ(float partialTicks) {
    return prevHeadPosZ + (headPosZ - prevHeadPosZ) * partialTicks;
  }

  public Stage getStage() {
    return stage;
  }

  private void setBoundaries() {
    // Original TileQuarry.setBoundaries(): take a landmark box that this quarry touches.
    IAreaProvider provider = Utils.getNearbyAreaProvider(getLevel(), worldPosition);

    if (provider != null) {
      int xSize = provider.xMax() - provider.xMin() + 1;
      int zSize = provider.zMax() - provider.zMin() + 1;
      if (xSize < 3 || zSize < 3) {
        // Original: chat.buildcraft.quarry.tooSmall, then fall back to the default area.
        notifyPlacer(Component.translatable("chat.bcreborn.quarry.tooSmall", xSize, zSize));
      } else {
        box.initialize(provider);
        box.createLaserData();
      }
      provider.removeFromWorld();
    }

    if (!box.isInitialized() || box.xMax - box.xMin < 2 || box.zMax - box.zMin < 2) {
      Direction facing = getBlockState().getValue(BuildCraftBlock.HORIZONTAL_FACING).getOpposite();
      int xMin = worldPosition.getX() - 5;
      int zMin = worldPosition.getZ() - 5;
      switch (facing) {
        case EAST -> xMin = worldPosition.getX() + 1;
        case WEST -> xMin = worldPosition.getX() - 11;
        case SOUTH -> zMin = worldPosition.getZ() + 1;
        default -> zMin = worldPosition.getZ() - 11;
      }
      box.initialize(xMin, worldPosition.getY(), zMin, xMin + 10, worldPosition.getY() + 4, zMin + 10);
    }
    box.yMax = Math.min(getLevel().getMaxBuildHeight() - 1, Math.max(box.yMax, box.yMin + 4));
    box.createLaserData();
    createFrameList();
    stage = Stage.BUILDING;
    setChanged();
  }

  @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    prevHeadPosX = headPosX;
    prevHeadPosY = headPosY;
    prevHeadPosZ = headPosZ;

    if (level.isClientSide) {
      if (stage == Stage.MOVING && headSpeed > 0) {
        moveHead(headSpeed);
      }
      return;
    }

    if (stage == Stage.DONE) {
      if (getControlMode() == Mode.Loop) {
        stage = Stage.IDLE;
      } else {
        return;
      }
    }
    if (!areChunksLoaded()) {
      return;
    }
    if (getControlMode() == Mode.Off && stage != Stage.MOVING) {
      return;
    }

    switch (stage) {
      case BUILDING -> {
        if (frameList.isEmpty()) {
          createFrameList();
        }

        if (frameList.isEmpty()) {
          stage = Stage.IDLE;
          setChanged();
          return;
        }

        buildNextFrame(level);
      }
      case DIGGING -> {
        dig();
      }
      case IDLE -> {
        if (findTarget(true)) {
          stage = Stage.MOVING;
          movingHorizontally = true;
          movingVertically = true;
          headTrajectory = (float) Math.atan2(targetZ + 0.5 - headPosZ, targetX + 0.5 - headPosX);
        } else {
          stage = Stage.DONE;
        }
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
      }
      case MOVING -> {
        // Original: useEnergy(20, ceil(20 + stored / 10)); speed = 0.1 + used / 2000, x0.7 in rain under open sky.
        int energyUsed = getBattery().useEnergy(20, (int) Math.ceil(20D + getBattery().getEnergyStored() / 10D), false);
        headSpeed = energyUsed >= 20 ? 0.1 + energyUsed / 2000.0 : 0;
        if (headSpeed > 0 && level.isRaining()) {
          BlockPos head = BlockPos.containing(headPosX, headPosY, headPosZ);
          if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, head.getX(), head.getZ()) < head.getY()) {
            headSpeed *= 0.7;
          }
        }
        if (headSpeed > 0) moveHead(headSpeed);
        if (stage != Stage.MOVING) headSpeed = 0;
        setChanged();
      }
      case DONE -> {
      }
    }
  }

  private void moveHead(double speed) {
    if (speed <= 0) return;
    if (movingHorizontally) {
      double dx = targetX + 0.5 - headPosX;
      double dz = targetZ + 0.5 - headPosZ;
      if (Math.abs(dx) < speed * 2 && Math.abs(dz) < speed * 2) {
        headPosX = targetX + 0.5;
        headPosZ = targetZ + 0.5;
        movingHorizontally = false;
        if (!movingVertically) {
          stage = Stage.DIGGING;
        }
      } else {
        headPosX += Math.cos(headTrajectory) * speed;
        headPosZ += Math.sin(headTrajectory) * speed;
      }
    }

    if (movingVertically) {
      double dy = targetY + 1.0 - headPosY;
      if (Math.abs(dy) < speed * 2) {
        headPosY = targetY + 1.0;
        movingVertically = false;
        if (!movingHorizontally) {
          stage = Stage.DIGGING;
        }
      } else {
        if (dy > 0) {
          headPosY += speed;
        } else {
          headPosY -= speed;
        }
      }
    }
  }

  /**
   * Original {@code TileQuarry.dig()}.
   */
  private void dig() {
    if (miner == null) {
      if (isQuarriableBlock(targetX, targetY, targetZ)) {
        miner = new BlockMiner(getLevel(), this, targetX, targetY, targetZ);
      } else {
        stage = Stage.IDLE;
        return;
      }
    }

    int rfTaken = miner.acceptEnergy(getBattery().getEnergyStored());
    getBattery().useEnergy(rfTaken, rfTaken, false);

    if (miner.hasMined()) {
      collectItemsAroundHead(miner);
    }

    if (miner.hasMined() || miner.hasFailed()) {
      miner = null;
      if (!findFrame()) {
        // A frame next to the quarry was broken: rebuild the frame before digging on.
        createFrameList();
        stage = Stage.BUILDING;
      } else {
        stage = Stage.IDLE;
      }
    }
  }

  /**
   * Original: after a successful mine, item entities around the head (-2..+3) are taken and output.
   */
  private void collectItemsAroundHead(BlockMiner miner) {
    AABB area = new AABB(headPosX - 2, headPosY - 2, headPosZ - 2, headPosX + 3, headPosY + 3, headPosZ + 3);
    for (ItemEntity entity : getLevel().getEntitiesOfClass(ItemEntity.class, area)) {
      if (!entity.isAlive()) continue;
      ItemStack stack = entity.getItem();
      if (stack.isEmpty()) continue;
      entity.discard();
      miner.mineStack(stack.copy());
    }
  }

  /**
   * Original {@code findFrame()}: the first horizontal neighbour of the quarry that lies in the box must be a frame.
   */
  private boolean findFrame() {
    for (Direction dir : Direction.Plane.HORIZONTAL) {
      BlockPos p = worldPosition.relative(dir);
      if (box.contains(p)) {
        return getLevel().getBlockState(p).getBlock() instanceof FrameBlock;
      }
    }
    return true;
  }

  /**
   * Places the next frame block. Replaceable blocks (air, plants, fluids) are overwritten, other blocks in the way
   * are mined with {@link BlockMiner} first (their drops are output), and the quarry waits on unbreakable ones.
   */
  private void buildNextFrame(Level level) {
    BlockPos framePos = frameList.peek();
    if (framePos == null || !level.isLoaded(framePos)) {
      return;
    }
    if (framePos.equals(worldPosition)) {
      // Never build over (or mine) the quarry itself.
      frameList.poll();
      finishFrameIfDone();
      return;
    }
    BlockState current = level.getBlockState(framePos);
    if (current.getBlock() instanceof FrameBlock) {
      frameList.poll();
      finishFrameIfDone();
      return;
    }

    if (!current.canBeReplaced()) {
      if (BlockUtils.isUnbreakableBlock(level, framePos) || level.getBlockEntity(framePos) != null) {
        // Keep unbreakable obstructions and blocks with a block entity (chests, machines) queued until the player
        // clears them; never finish an incomplete frame.
        return;
      }
      if (frameMiner == null) {
        frameMiner = new BlockMiner(level, this, framePos.getX(), framePos.getY(), framePos.getZ());
      }
      int rfTaken = frameMiner.acceptEnergy(getBattery().getEnergyStored());
      getBattery().useEnergy(rfTaken, rfTaken, false);
      if (frameMiner.hasFailed()) {
        frameMiner = null;
        frameList.poll();
        finishFrameIfDone();
      } else if (frameMiner.hasMined()) {
        frameMiner = null;
      }
      setChanged();
      return;
    }

    if (getBattery().getEnergyStored() < FRAME_ENERGY) {
      return;
    }
    if (!level.setBlock(framePos, BuildersBlock.FRAME.get().defaultBlockState(), 3)) {
      return;
    }
    getBattery().useEnergy(FRAME_ENERGY, FRAME_ENERGY, false);
    frameList.poll();
    finishFrameIfDone();
  }

  private void finishFrameIfDone() {
    if (frameList.isEmpty()) {
      stage = Stage.IDLE;
    }
    setChanged();
  }

  /**
   * Original {@code areChunksLoaded()}: pause while any chunk of the box is unloaded, so the quarry never
   * loads chunks by itself.
   */
  private boolean areChunksLoaded() {
    if (!box.isInitialized()) return true;
    for (int cx = box.xMin >> 4; cx <= box.xMax >> 4; cx++) {
      for (int cz = box.zMin >> 4; cz <= box.zMax >> 4; cz++) {
        if (!getLevel().hasChunk(cx, cz)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Original {@code BlockQuarry.onBlockActivated()} with a wrench: {@code reinitalize()} rebuilds the frame.
   */
  public void reinitialize() {
    if (getLevel() == null || getLevel().isClientSide) return;
    miner = null;
    frameMiner = null;
    visitList.clear();
    createFrameList();
    stage = Stage.BUILDING;
    headSpeed = 0;
    setChanged();
  }

  public void setPlacedBy(@Nullable Player player) {
    placedBy = player == null ? null : player.getUUID();
  }

  private void notifyPlacer(Component message) {
    if (placedBy != null && getLevel() instanceof ServerLevel server) {
      ServerPlayer player = server.getServer().getPlayerList().getPlayer(placedBy);
      if (player != null) {
        player.sendSystemMessage(message);
      }
    }
  }

  /**
   * Original {@code destroy()}: clears the block-breaking overlay of an unfinished mine.
   */
  public void destroy() {
    if (miner != null) miner.invalidate();
    if (frameMiner != null) frameMiner.invalidate();
    miner = null;
    frameMiner = null;
  }

  @Override
  public Mode getControlMode() {
    return mode == null ? Mode.On : mode;
  }

  @Override
  public void setControlMode(Mode mode) {
    this.mode = mode;
    setChanged();
  }

  @Override
  public boolean acceptsControlMode(Mode mode) {
    return mode == Mode.Off || mode == Mode.On || mode == Mode.Loop;
  }

  /**
   * Original {@code findTarget()}. When the list was not just rebuilt, the column above the next target is
   * re-checked so blocks placed since then are dug first.
   */
  public boolean findTarget(boolean doSet) {
    boolean rebuilt = false;
    if (visitList.isEmpty()) {
      createColumnVisitList();
      rebuilt = true;
    }

    if (!doSet) {
      return !visitList.isEmpty();
    }

    if (visitList.isEmpty()) {
      return false;
    }

    int[] nextTarget = visitList.removeFirst();
    if (!rebuilt) {
      for (int y = nextTarget[1] + 1; y < worldPosition.getY() + 3; y++) {
        if (isQuarriableBlock(nextTarget[0], y, nextTarget[2])) {
          createColumnVisitList();
          if (visitList.isEmpty()) {
            return false;
          }
          nextTarget = visitList.removeFirst();
          break;
        }
      }
    }

    targetX = nextTarget[0];
    targetY = nextTarget[1];
    targetZ = nextTarget[2];
    return true;
  }

  private void createColumnVisitList() {
    visitList.clear();
    int sizeX = box.xMax - box.xMin - 1;
    int sizeZ = box.zMax - box.zMin - 1;
    if (sizeX <= 0 || sizeZ <= 0) return;
    boolean[][] blockedColumns = new boolean[sizeX][sizeZ];
    int maxY = Math.min(worldPosition.getY() + 3, getLevel().getMaxBuildHeight() - 1);
    for (int y = maxY; y >= getLevel().getMinBuildHeight(); y--) {
      for (int ix = 0; ix < sizeX; ix++) {
        int x = (y & 1) == 0 ? ix : sizeX - 1 - ix;
        for (int iz = 0; iz < sizeZ; iz++) {
          int z = (x & 1) == (y & 1) ? iz : sizeZ - 1 - iz;
          if (blockedColumns[x][z]) continue;
          int bx = box.xMin + x + 1;
          int bz = box.zMin + z + 1;
          BlockPos pos = new BlockPos(bx, y, bz);
          if (BlockUtils.isUnbreakableBlock(getLevel(), pos)) {
            blockedColumns[x][z] = true;
          } else if (isQuarriableBlock(bx, y, bz)) {
            visitList.add(new int[]{bx, y, bz});
          }
        }
      }
      if (!visitList.isEmpty()) break;
    }
  }
  private void createFrameList() {
    frameList.clear();
    if (!box.isInitialized()) return;

    List<BlockPos> list = new ArrayList<>();
    int yMax = Math.min(box.yMax, getLevel().getMaxBuildHeight() - 1);

    // Horizontal frames at top
    for (int x = box.xMin; x <= box.xMax; x++) {
      list.add(new BlockPos(x, yMax, box.zMin));
      list.add(new BlockPos(x, yMax, box.zMax));
      list.add(new BlockPos(x, box.yMin, box.zMin));
      list.add(new BlockPos(x, box.yMin, box.zMax));
    }
    for (int z = box.zMin + 1; z < box.zMax; z++) {
      list.add(new BlockPos(box.xMin, yMax, z));
      list.add(new BlockPos(box.xMax, yMax, z));
      list.add(new BlockPos(box.xMin, box.yMin, z));
      list.add(new BlockPos(box.xMax, box.yMin, z));
    }

    // Vertical frames
    for (int y = box.yMin + 1; y < Math.min(box.yMax, getLevel().getMaxBuildHeight() - 1); y++) {
      list.add(new BlockPos(box.xMin, y, box.zMin));
      list.add(new BlockPos(box.xMax, y, box.zMin));
      list.add(new BlockPos(box.xMin, y, box.zMax));
      list.add(new BlockPos(box.xMax, y, box.zMax));
    }

    // Sort by distance to quarry
    list.sort(Comparator.comparingDouble(p -> p.distSqr(worldPosition)));

    frameList.addAll(list);
  }

  private boolean isQuarriableBlock(int bx, int by, int bz) {
    BlockPos pos = new BlockPos(bx, by, bz);
    if (pos.equals(worldPosition)) return false;
    BlockState state = getLevel().getBlockState(pos);
    if (state.getBlock() == BuildersBlock.FRAME.get()) return false;
    if (state.isAir()) return false;
    // We want to skip liquids and mine blocks below them
    if (!state.getFluidState().isEmpty() && state.equals(state.getFluidState().createLegacyBlock())) return false;
    Block block = state.getBlock();
    // Original BuildCraft logic also excludes Fluid blocks unless specifically handled
    return !BlockUtils.isUnbreakableBlock(getLevel(), pos, block);
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    if (nbt.contains("box")) {
      box.initialize(nbt.getCompound("box"));
    }
    stage = Stage.values()[nbt.getInt("stage")];
    targetX = nbt.getInt("targetX");
    targetY = nbt.getInt("targetY");
    targetZ = nbt.getInt("targetZ");
    restoredHead = nbt.contains("headPosX");
    headSpeed = nbt.getDouble("headSpeed");
    headPosX = nbt.getDouble("headPosX");
    headPosY = nbt.getDouble("headPosY");
    headPosZ = nbt.getDouble("headPosZ");
    prevHeadPosX = headPosX;
    prevHeadPosY = headPosY;
    prevHeadPosZ = headPosZ;
    headTrajectory = nbt.getFloat("headTrajectory");
    movingHorizontally = nbt.getBoolean("movingHorizontally");
    movingVertically = nbt.getBoolean("movingVertically");
    placedBy = nbt.hasUUID("placedBy") ? nbt.getUUID("placedBy") : null;

    if (nbt.contains("frameList")) {
      frameList.clear();
      long[] frames = nbt.getLongArray("frameList");
      for (long f : frames) {
        frameList.add(BlockPos.of(f));
      }
    }
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    CompoundTag boxTag = new CompoundTag();
    box.writeTag(boxTag);
    nbt.put("box", boxTag);
    nbt.putInt("stage", stage.ordinal());
    nbt.putInt("targetX", targetX);
    nbt.putInt("targetY", targetY);
    nbt.putInt("targetZ", targetZ);
    nbt.putDouble("headSpeed", headSpeed);
    nbt.putDouble("headPosX", headPosX);
    nbt.putDouble("headPosY", headPosY);
    nbt.putDouble("headPosZ", headPosZ);
    nbt.putFloat("headTrajectory", headTrajectory);
    nbt.putBoolean("movingHorizontally", movingHorizontally);
    nbt.putBoolean("movingVertically", movingVertically);
    if (placedBy != null) {
      nbt.putUUID("placedBy", placedBy);
    }

    if (!frameList.isEmpty()) {
      long[] frames = new long[frameList.size()];
      int i = 0;
      for (BlockPos p : frameList) {
        frames[i++] = p.asLong();
      }
      nbt.putLongArray("frameList", frames);
    }
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    super.writeData(data);
    box.writeData(data);
    data.writeEnum(stage);
    data.writeDouble(headSpeed);
    data.writeDouble(headPosX);
    data.writeDouble(headPosY);
    data.writeDouble(headPosZ);
    data.writeFloat(headTrajectory);
    data.writeBoolean(movingHorizontally);
    data.writeBoolean(movingVertically);
    data.writeInt(targetX);
    data.writeInt(targetY);
    data.writeInt(targetZ);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    super.readData(data);
    box.readData(data);
    stage = data.readEnum(Stage.class);
    headSpeed = data.readDouble();
    headPosX = data.readDouble();
    headPosY = data.readDouble();
    headPosZ = data.readDouble();
    headTrajectory = data.readFloat();
    movingHorizontally = data.readBoolean();
    movingVertically = data.readBoolean();
    targetX = data.readInt();
    targetY = data.readInt();
    targetZ = data.readInt();
    if (level != null && level.isClientSide) {
      if (box.isInitialized()) {
        box.createLaserData();
      }
    }
  }

  @Override
  public AABB getRenderBoundingBox() {
    return box.getBoundingBox().inflate(1.0);
  }

  public Container getInventory() {
    return inv;
  }

  @Override
  public List<ItemStack> getInventoryList() {
    List<ItemStack> list = new ArrayList<>();
    for (int i = 0; i < inv.getContainerSize(); i++) {
      list.add(inv.getItem(i));
    }
    return list;
  }

  @Override
  public void setRemoved() {
    destroy();
    super.setRemoved();
  }

  @Override
  public EnergyStorage getEnergyStorage() {
    return Objects.requireNonNull(getBattery());
  }

  /** Energy per placed frame block (the original builder charged per placed block). */
  private static final int FRAME_ENERGY = 25;

  public enum Stage {
    BUILDING,
    DIGGING,
    MOVING,
    IDLE,
    DONE
  }
}
