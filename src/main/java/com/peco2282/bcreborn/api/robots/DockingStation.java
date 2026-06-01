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
package com.peco2282.bcreborn.api.robots;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.IInjectable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class DockingStation {
  public Direction side;
  public Level world;

  private long robotTakingId = RobotEntityBase.NULL_ROBOT_ID;
  private RobotEntityBase robotTaking;

  private boolean linkIsMain = false;

  private BlockIndex index;

  public DockingStation(BlockIndex iIndex, Direction iSide) {
    index = iIndex;
    side = iSide;
  }

  public DockingStation() {
  }

  public boolean isMainStation() {
    return linkIsMain;
  }

  public int x() {
    return index.x;
  }

  public int y() {
    return index.y;
  }

  public int z() {
    return index.z;
  }

  public Direction side() {
    return side;
  }

  public RobotEntityBase robotTaking() {
    if (robotTakingId == RobotEntityBase.NULL_ROBOT_ID) {
      return null;
    } else if (robotTaking == null) {
      robotTaking = RobotManager.registryProvider.getRegistry(world).getLoadedRobot(
        robotTakingId);
    }

    return robotTaking;
  }

  public void invalidateRobotTakingEntity() {
    robotTaking = null;
  }

  public long linkedId() {
    return robotTakingId;
  }

  public boolean takeAsMain(RobotEntityBase robot) {
    if (robotTakingId == RobotEntityBase.NULL_ROBOT_ID) {
      IRobotRegistry registry = RobotManager.registryProvider.getRegistry(world);
      linkIsMain = true;
      robotTaking = robot;
      robotTakingId = robot.getRobotId();
      registry.registryMarkDirty();
      robot.setMainStation(this);
      registry.take(this, robot.getRobotId());

      return true;
    } else {
      return robotTakingId == robot.getRobotId();
    }
  }

  public boolean take(RobotEntityBase robot) {
    if (robotTaking == null) {
      IRobotRegistry registry = RobotManager.registryProvider.getRegistry(world);
      linkIsMain = false;
      robotTaking = robot;
      robotTakingId = robot.getRobotId();
      registry.registryMarkDirty();
      registry.take(this, robot.getRobotId());

      return true;
    } else {
      return robot.getRobotId() == robotTakingId;
    }
  }

  public void release(RobotEntityBase robot) {
    if (robotTaking == robot && !linkIsMain) {
      IRobotRegistry registry = RobotManager.registryProvider.getRegistry(world);
      unsafeRelease(robot);
      registry.registryMarkDirty();
      registry.release(this, robot.getRobotId());
    }
  }

  /**
   * Same a release but doesn't clear the registry (presumably called from the
   * registry).
   */
  public void unsafeRelease(RobotEntityBase robot) {
    if (robotTaking == robot) {
      linkIsMain = false;
      robotTaking = null;
      robotTakingId = RobotEntityBase.NULL_ROBOT_ID;
    }
  }

  public void writeToNBT(CompoundTag nbt) {
    CompoundTag indexNBT = new CompoundTag();
    index.writeTo(indexNBT);
    nbt.put("index", indexNBT);
    nbt.putByte("side", (byte) side.ordinal());
    nbt.putBoolean("isMain", linkIsMain);
    nbt.putLong("robotId", robotTakingId);
  }

  public void readFromNBT(CompoundTag nbt) {
    index = new BlockIndex(nbt.getCompound("index"));
    side = Direction.values()[nbt.getByte("side")];
    linkIsMain = nbt.getBoolean("isMain");
    robotTakingId = nbt.getLong("robotId");
  }

  public boolean isTaken() {
    return robotTakingId != RobotEntityBase.NULL_ROBOT_ID;
  }

  public long robotIdTaking() {
    return robotTakingId;
  }

  public BlockIndex index() {
    return index;
  }

  @Override
  public String toString() {
    return "{" + index.x + ", " + index.y + ", " + index.z + ", " + side + " :" + robotTakingId
      + "}";
  }

  public boolean linkIsDocked() {
    if (robotTaking() != null) {
      return robotTaking().getDockingStation() == this;
    } else {
      return false;
    }
  }

  public boolean canRelease() {
    return !isMainStation() && !linkIsDocked();
  }

  public boolean isInitialized() {
    return true;
  }

  public abstract Iterable<StatementSlot> getActiveActions();

  public IInjectable getItemOutput() {
    return null;
  }

  public Direction getItemOutputSide() {
    return Direction.UP;
  }

  public Container getItemInput() {
    return null;
  }

  public Direction getItemInputSide() {
    return Direction.UP;
  }

  public IFluidHandler getFluidOutput() {
    return null;
  }

  public Direction getFluidOutputSide() {
    return Direction.UP;
  }

  public IFluidHandler getFluidInput() {
    return null;
  }

  public Direction getFluidInputSide() {
    return Direction.UP;
  }

  public boolean providesPower() {
    return false;
  }

  public IRequestProvider getRequestProvider() {
    return null;
  }

  public void onChunkUnload() {

  }
}
