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
import com.peco2282.bcreborn.builders.BuildersConfig;
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

import java.util.*;

public class QuarryBlockEntity extends AbstractBuilderBlockEntity implements IBoxProvider {
  public enum Stage {
    BUILDING,
    DIGGING,
    MOVING,
    IDLE,
    DONE
  }

  private final SimpleInventory inv = new SimpleInventory(18, "Quarry", 64);
  public Box box = new Box();
  private Stage stage = Stage.BUILDING;

  private int targetX, targetY, targetZ;
  private double headPosX, headPosY, headPosZ;
  private double prevHeadPosX, prevHeadPosY, prevHeadPosZ;
  private float headTrajectory;
  private boolean movingHorizontally, movingVertically;

  private BlockMiner miner;
  private final Deque<int[]> visitList = new LinkedList<>();
  private final Deque<BlockPos> frameList = new LinkedList<>();

  public QuarryBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.QUARRY.get(), pos, state);
    box.kind = Box.Kind.STRIPES;
    setBattery(new EnergyStorage(10000, 1000, 1000));
  }

  @Override
  public void initialize() {
    if (!getLevel().isClientSide) {
      if (!box.isInitialized()) {
        setBoundaries();
      }
      headPosX = worldPosition.getX() + 0.5;
      headPosY = worldPosition.getY() + 1.0;
      headPosZ = worldPosition.getZ() + 0.5;
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

    if (!box.isInitialized()) {
      // Default boundaries logic (optional, BuildCraft uses 11x11 by default)
      int xMin = worldPosition.getX() - 5;
      int zMin = worldPosition.getZ() - 5;
      box.initialize(xMin, worldPosition.getY(), zMin, xMin + 10, worldPosition.getY() + 5, zMin + 10);
      box.createLaserData();
    }
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
      if (stage != Stage.DONE) {
        moveHead(0.1); // Constant speed for client side prediction
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
          if (framePos != null) {
            if (level.getBlockState(framePos).isAir() || level.getBlockState(framePos).getBlock() instanceof FrameBlock) {
              getBattery().useEnergy(energyNeeded, energyNeeded, false);
              level.setBlock(framePos, BuildersBlock.FRAME.get().defaultBlockState(), 3);
              frameList.poll();
              if (frameList.isEmpty()) {
                stage = Stage.IDLE;
              }
              setChanged();
            } else {
              // Something is in the way, skip this frame or wait?
              // BuildCraft usually breaks the block if it's not unbreakable
              frameList.poll();
              if (frameList.isEmpty()) {
                stage = Stage.IDLE;
              }
              setChanged();
            }
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
        int energyUsed = getBattery().useEnergy(20, 100, false);
        if (energyUsed >= 20) {
          moveHead(0.1 + energyUsed / 1000.0);
        }
      }
      case DONE -> {
      }
    }
  }

  private void moveHead(double speed) {
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
      if (findTarget(false)) { // Check if we still have targets
        miner = new BlockMiner(getLevel(), this, targetX, targetY, targetZ);
      } else {
        stage = Stage.DONE;
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
    int sizeX = box.xMax - box.xMin + 1;
    int sizeZ = box.zMax - box.zMin + 1;
    boolean[][] blockedColumns = new boolean[sizeX][sizeZ];

    int maxY = Math.min(box.yMax, BuildersConfig.getMiningDepth());
    for (int searchY = maxY; searchY >= getLevel().getMinBuildHeight(); --searchY) {
      for (int searchX = 0; searchX < sizeX; searchX++) {
        for (int searchZ = 0; searchZ < sizeZ; searchZ++) {
          if (!blockedColumns[searchX][searchZ]) {
            int bx = box.xMin + searchX;
            int bz = box.zMin + searchZ;

            if (isQuarriableBlock(bx, searchY, bz)) {
              visitList.add(new int[]{bx, searchY, bz});
            } else {
              BlockPos pos = new BlockPos(bx, searchY, bz);
              BlockState state = getLevel().getBlockState(pos);
              if (!state.isAir() && state.getFluidState().isEmpty()) {
                // If it's not quarriable, not air, and not a fluid, it might be unbreakable
                if (BlockUtils.isUnbreakableBlock(getLevel(), pos)) {
                  blockedColumns[searchX][searchZ] = true;
                }
              }
              // If it's a fluid, we don't block the column, so we can search below it in the next Y iteration.
            }
          }
        }
      }
      if (!visitList.isEmpty()) {
        break; // Process one layer at a time like BuildCraft
      }
    }
  }

  private void createFrameList() {
    frameList.clear();
    if (!box.isInitialized()) return;

    List<BlockPos> list = new ArrayList<>();
    int yMax = Math.min(box.yMax, BuildersConfig.getMiningDepth());

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
    for (int y = box.yMin + 1; y < Math.min(box.yMax, BuildersConfig.getMiningDepth()); y++) {
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
    BlockState state = getLevel().getBlockState(pos);
    if (state.getBlock() == BuildersBlock.FRAME.get()) return false;
    if (state.isAir()) return false;
    // We want to skip liquids and mine blocks below them
    if (!state.getFluidState().isEmpty()) return false;
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
    headPosX = nbt.getDouble("headPosX");
    headPosY = nbt.getDouble("headPosY");
    headPosZ = nbt.getDouble("headPosZ");
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
    box.writeToNBT(boxTag);
    nbt.put("box", boxTag);
    nbt.putInt("stage", stage.ordinal());
    nbt.putInt("targetX", targetX);
    nbt.putInt("targetY", targetY);
    nbt.putInt("targetZ", targetZ);
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
  public EnergyStorage getBattery() {
    return Objects.requireNonNull(super.getBattery());
  }
}
