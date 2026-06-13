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
package com.peco2282.bcreborn.silicon.block.entity;

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.api.power.ILaserTarget;
import com.peco2282.bcreborn.api.power.ILaserTargetBlock;
import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.block.LaserBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.LinkedList;
import java.util.List;

public class LaserBlockEntity extends BuildCraftBlockEntity implements IHasWork, IControllable {

  private static final float LASER_OFFSET = 2.0F / 16.0F;
  private final SafeTimeTracker laserTickTracker = new SafeTimeTracker(10);
  private final SafeTimeTracker searchTracker = new SafeTimeTracker(100, 100);
  public LaserData laser = new LaserData();
  private ILaserTarget laserTarget;

  public LaserBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.LASER.get(), pos, state);
    this.setBattery(new EnergyStorage(10000, 250, 0));
  }

  @Override
  public void initialize() {
    super.initialize();

    if (laser == null) {
      laser = new LaserData();
    }

    laser.isVisible = false;
    laser.head = new Position(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    laser.tail = new Position(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    laser.isGlowing = true;
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    laser.iterateTexture();

    if (level.isClientSide) {
      return;
    }

    if (mode == Mode.Off) {
      removeLaser();
      return;
    }

    if (canFindTable()) {
      findTable();
    }

    if (!isValidTable()) {
      removeLaser();
      return;
    }

    if (getBattery().getEnergyStored() == 0) {
      removeLaser();
      return;
    }

    if (laser != null) {
      laser.isVisible = true;

      if (canUpdateLaser()) {
        updateLaser();
      }

      int maxPower = getMaxPowerSent();
      int powerToTransfer = getBattery().useEnergy(0, maxPower, true);

      if (powerToTransfer > 0) {
        laserTarget.receiveLaserEnergy(powerToTransfer);
        getBattery().useEnergy(0, powerToTransfer, false);
        onPowerSent(powerToTransfer);
      }
    }
  }

  protected int getMaxPowerSent() {
    return 40;
  }

  protected void onPowerSent(int power) {
  }

  protected boolean canFindTable() {
    return searchTracker.markTimeIfDelay(level);
  }

  protected boolean canUpdateLaser() {
    return laserTickTracker.markTimeIfDelay(level);
  }

  protected boolean isValidTable() {
    if (laserTarget == null || laserTarget.isInvalidTarget()) {
      return false;
    }
    if (!laserTarget.requiresLaserEnergy()) {
      return false;
    }

    BlockEntity tile = level.getBlockEntity(new BlockPos((int) laserTarget.getXCoord(), (int) laserTarget.getYCoord(), (int) laserTarget.getZCoord()));
    return tile == laserTarget;
  }

  protected void findTable() {
    laserTarget = null;

    List<ILaserTarget> targets = new LinkedList<>();

    for (int x = worldPosition.getX() - 5; x <= worldPosition.getX() + 5; x++) {
      for (int y = worldPosition.getY() - 5; y <= worldPosition.getY() + 5; y++) {
        for (int z = worldPosition.getZ() - 5; z <= worldPosition.getZ() + 5; z++) {
          BlockPos p = new BlockPos(x, y, z);
          if (level.getBlockState(p).getBlock() instanceof ILaserTargetBlock) {
            BlockEntity tile = level.getBlockEntity(p);

            if (tile instanceof ILaserTarget table) {
              if (table.requiresLaserEnergy()) {
                targets.add(table);
              }
            }
          }
        }
      }
    }

    double minDistance = Double.MAX_VALUE;

    for (ILaserTarget target : targets) {
      double distance = worldPosition.distSqr(new BlockPos((int) target.getXCoord(), (int) target.getYCoord(), (int) target.getZCoord()));

      if (distance < minDistance) {
        minDistance = distance;
        laserTarget = target;
      }
    }

    if (laserTarget != null) {
      updateLaser();
    }
  }

  protected void updateLaser() {
    if (laserTarget == null) {
      return;
    }

    laser.head = new Position(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);

    switch (getBlockState().getValue(LaserBlock.FACING)) {
      case DOWN -> laser.head.y -= LASER_OFFSET;
      case UP -> laser.head.y += LASER_OFFSET;
      case NORTH -> laser.head.z -= LASER_OFFSET;
      case SOUTH -> laser.head.z += LASER_OFFSET;
      case WEST -> laser.head.x -= LASER_OFFSET;
      case EAST -> laser.head.x += LASER_OFFSET;
    }

    laser.tail = new Position(laserTarget.getXCoord() + 0.5, laserTarget.getYCoord() + 0.5, laserTarget.getZCoord() + 0.5);
    laser.isVisible = true;

    sendNetworkUpdate();
  }

  protected void removeLaser() {
    if (laser.isVisible) {
      laser.isVisible = false;
      sendNetworkUpdate();
    }
  }

  protected void sendNetworkUpdate() {
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    if (nbt.contains("laser")) {
      laser.readFromNBT(nbt.getCompound("laser"));
    }
  }

  @Override
  public void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    CompoundTag laserNbt = new CompoundTag();
    laser.writeToNBT(laserNbt);
    nbt.put("laser", laserNbt);
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    super.readData(stream);
    laser.readData(stream);
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    super.writeData(stream);
    laser.writeData(stream);
  }

  @Override
  public boolean hasWork() {
    return isValidTable() && getBattery().getEnergyStored() > 0;
  }

  @Override
  public AABB getRenderBoundingBox() {
    return new AABB(worldPosition).inflate(10);
  }

  @Override
  public Mode getControlMode() {
    return mode;
  }

  @Override
  public void setControlMode(Mode mode) {
    this.mode = mode;
  }

  @Override
  public boolean acceptsControlMode(Mode mode) {
    return mode == Mode.On || mode == Mode.Off;
  }
}
