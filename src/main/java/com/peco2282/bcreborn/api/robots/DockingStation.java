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

/**
 * Represents a location where a robot can dock and interact with the world.
 */
public abstract class DockingStation {
  /**
   * The side of the block this docking station is on.
   */
  public Direction side;

  /**
   * The world this docking station is in.
   */
  public Level world;

  private long robotTakingId = RobotEntityBase.NULL_ROBOT_ID;
  private RobotEntityBase robotTaking;

  private boolean linkIsMain = false;

  private BlockIndex index;

  /**
   * Constructs a new DockingStation with the specified index and side.
   *
   * @param iIndex The block position index.
   * @param iSide  The side of the block.
   */
  public DockingStation(BlockIndex iIndex, Direction iSide) {
    index = iIndex;
    side = iSide;
  }

  /**
   * Default constructor for NBT loading.
   */
  public DockingStation() {
  }

  /**
   * Checks if this station is the main station for the robot that has taken it.
   *
   * @return {@code true} if it's the main station, {@code false} otherwise.
   */
  public boolean isMainStation() {
    return linkIsMain;
  }

  /**
   * Returns the X coordinate of this station.
   *
   * @return The X coordinate.
   */
  public int x() {
    return index.x;
  }

  /**
   * Returns the Y coordinate of this station.
   *
   * @return The Y coordinate.
   */
  public int y() {
    return index.y;
  }

  /**
   * Returns the Z coordinate of this station.
   *
   * @return The Z coordinate.
   */
  public int z() {
    return index.z;
  }

  /**
   * Returns the side of the block this station is on.
   *
   * @return The side.
   */
  public Direction side() {
    return side;
  }

  /**
   * Returns the robot entity that has currently taken or is linked to this station.
   *
   * @return The robot entity, or {@code null} if none.
   */
  public RobotEntityBase robotTaking() {
    if (robotTakingId == RobotEntityBase.NULL_ROBOT_ID) {
      return null;
    } else if (robotTaking == null) {
      robotTaking = RobotManager.registryProvider.getRegistry(world).getLoadedRobot(
        robotTakingId);
    }

    return robotTaking;
  }

  /**
   * Invalidates the cached robot entity.
   */
  public void invalidateRobotTakingEntity() {
    robotTaking = null;
  }

  /**
   * Returns the ID of the linked robot.
   *
   * @return The robot ID.
   */
  public long linkedId() {
    return robotTakingId;
  }

  /**
   * Attempts to link a robot to this station as its main station.
   *
   * @param robot The robot to link.
   * @return {@code true} if successful or already linked, {@code false} otherwise.
   */
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

  /**
   * Attempts to link a robot to this station.
   *
   * @param robot The robot to link.
   * @return {@code true} if successful or already linked, {@code false} otherwise.
   */
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

  /**
   * Releases the linked robot from this station.
   *
   * @param robot The robot to release.
   */
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

  /**
   * Writes the station's state to NBT.
   *
   * @param nbt The NBT tag to write to.
   */
  public void writeToNBT(CompoundTag nbt) {
    CompoundTag indexNBT = new CompoundTag();
    index.writeTo(indexNBT);
    nbt.put("index", indexNBT);
    nbt.putByte("side", (byte) side.ordinal());
    nbt.putBoolean("isMain", linkIsMain);
    nbt.putLong("robotId", robotTakingId);
  }

  /**
   * Reads the station's state from NBT.
   *
   * @param nbt The NBT tag to read from.
   */
  public void readFromNBT(CompoundTag nbt) {
    index = new BlockIndex(nbt.getCompound("index"));
    side = Direction.values()[nbt.getByte("side")];
    linkIsMain = nbt.getBoolean("isMain");
    robotTakingId = nbt.getLong("robotId");
  }

  /**
   * Checks if the station is currently taken by a robot.
   *
   * @return {@code true} if taken, {@code false} otherwise.
   */
  public boolean isTaken() {
    return robotTakingId != RobotEntityBase.NULL_ROBOT_ID;
  }

  /**
   * Returns the ID of the robot currently taking this station.
   *
   * @return The robot ID.
   */
  public long robotIdTaking() {
    return robotTakingId;
  }

  /**
   * Returns the block index (position) of this station.
   *
   * @return The block index.
   */
  public BlockIndex index() {
    return index;
  }

  @Override
  public String toString() {
    return "{" + index.x + ", " + index.y + ", " + index.z + ", " + side + " :" + robotTakingId
      + "}";
  }

  /**
   * Checks if the linked robot is currently docked at this station.
   *
   * @return {@code true} if docked, {@code false} otherwise.
   */
  public boolean linkIsDocked() {
    if (robotTaking() != null) {
      return robotTaking().getDockingStation() == this;
    } else {
      return false;
    }
  }

  /**
   * Checks if this station can be released by the current robot.
   *
   * @return {@code true} if it can be released, {@code false} otherwise.
   */
  public boolean canRelease() {
    return !isMainStation() && !linkIsDocked();
  }

  /**
   * Checks if the station is initialized.
   *
   * @return {@code true} if initialized, {@code false} otherwise.
   */
  public boolean isInitialized() {
    return true;
  }

  /**
   * Returns the active statement actions for this station.
   *
   * @return An iterable of active statement slots.
   */
  public abstract Iterable<StatementSlot> getActiveActions();

  /**
   * Returns the item output handler for this station.
   *
   * @return The item output handler, or {@code null} if none.
   */
  public IInjectable getItemOutput() {
    return null;
  }

  /**
   * Returns the side to which items are output.
   *
   * @return The output side.
   */
  public Direction getItemOutputSide() {
    return Direction.UP;
  }

  /**
   * Returns the item input container for this station.
   *
   * @return The item input container, or {@code null} if none.
   */
  public Container getItemInput() {
    return null;
  }

  /**
   * Returns the side from which items are input.
   *
   * @return The input side.
   */
  public Direction getItemInputSide() {
    return Direction.UP;
  }

  /**
   * Returns the fluid output handler for this station.
   *
   * @return The fluid output handler, or {@code null} if none.
   */
  public IFluidHandler getFluidOutput() {
    return null;
  }

  /**
   * Returns the side to which fluids are output.
   *
   * @return The output side.
   */
  public Direction getFluidOutputSide() {
    return Direction.UP;
  }

  /**
   * Returns the fluid input handler for this station.
   *
   * @return The fluid input handler, or {@code null} if none.
   */
  public IFluidHandler getFluidInput() {
    return null;
  }

  /**
   * Returns the side from which fluids are input.
   *
   * @return The input side.
   */
  public Direction getFluidInputSide() {
    return Direction.UP;
  }

  /**
   * Checks if this station provides power to the docked robot.
   *
   * @return {@code true} if it provides power, {@code false} otherwise.
   */
  public boolean providesPower() {
    return false;
  }

  /**
   * Returns the request provider for this station.
   *
   * @return The request provider, or {@code null} if none.
   */
  public IRequestProvider getRequestProvider() {
    return null;
  }

  /**
   * Called when the chunk containing this station is unloaded.
   */
  public void onChunkUnload() {
  }
}
