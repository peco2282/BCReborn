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

import com.peco2282.bcreborn.api.core.IAreaProvider;
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
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;

import java.util.*;

public class QuarryBlockEntity extends AbstractBuilderBlockEntity implements IBoxProvider {
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

  public QuarryBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.QUARRY.get(), pos, state);
    box.kind = Box.Kind.STRIPES;
    setBattery(new EnergyStorage(10000, 1000, 1000));
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
    IAreaProvider provider = null;
    for (Direction side : Direction.values()) {
      BlockEntity tile = getLevel().getBlockEntity(worldPosition.relative(side));
      if (tile instanceof IAreaProvider a) {
        provider = a;
        break;
      }
    }

    if (provider != null) {
      box.initialize(provider);
      if (box.isInitialized()) {
        box.createLaserData();
        provider.removeFromWorld();
      }
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

        int energyNeeded = 25;
        if (getBattery().getEnergyStored() >= energyNeeded) {
          BlockPos framePos = frameList.peek();
          if (framePos != null && level.hasChunkAt(framePos)) {
            if (level.getBlockState(framePos).isAir() || level.getBlockState(framePos).getBlock() instanceof FrameBlock) {
              if (!(level.getBlockState(framePos).getBlock() instanceof FrameBlock)) {
                if (!level.setBlock(framePos, BuildersBlock.FRAME.get().defaultBlockState(), 3)) return;
                getBattery().useEnergy(energyNeeded, energyNeeded, false);
              }
              frameList.poll();
              if (frameList.isEmpty()) {
                stage = Stage.IDLE;
              }
              setChanged();
            }
            // Keep obstructed positions queued until cleared; never finish an incomplete frame.
          }
        }
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
        int energyUsed = getBattery().useEnergy(20, 200, false);
        headSpeed = energyUsed >= 20 ? 0.1 + energyUsed / 2000.0 : 0;
        if (movingHorizontally && movingVertically) headSpeed *= 0.7;
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

  private void dig() {
    if (miner == null || miner.hasMined() || miner.hasFailed()) {
      if (miner != null && (miner.hasMined() || miner.hasFailed())) {
        miner = null;
        stage = Stage.IDLE;
        return;
      }
      if (isQuarriableBlock(targetX, targetY, targetZ)) {
        miner = new BlockMiner(getLevel(), this, targetX, targetY, targetZ);
      } else {
        stage = Stage.IDLE;
        return;
      }
    }

    // Energy consumption
    int rfTaken = miner.acceptEnergy(getBattery().getEnergyStored());
    getBattery().useEnergy(rfTaken, rfTaken, false);
  }

  public boolean findTarget(boolean doSet) {
    if (visitList.isEmpty()) {
      createColumnVisitList();
    }

    if (!doSet) {
      return !visitList.isEmpty();
    }

    if (visitList.isEmpty()) {
      return false;
    }

    int[] nextTarget = visitList.removeFirst();
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
    super.setRemoved();
  }

  @Override
  public EnergyStorage getEnergyStorage() {
    return Objects.requireNonNull(getBattery());
  }

  public enum Stage {
    BUILDING,
    DIGGING,
    MOVING,
    IDLE,
    DONE
  }
}
