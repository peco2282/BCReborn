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

import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.IInjectable;
import com.peco2282.bcreborn.common.nbt.NbtReader;
import com.peco2282.bcreborn.common.nbt.NbtWriter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a location where a robot can dock and interact with the world.
 */
public abstract class DockingStation<T extends DockingStation<T>> implements INBTSerializable {
  protected final DockingStationType<T> type;
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
  private BlockPos pos;

  /**
   * Constructs a new DockingStation with the specified pos and side.
   *
   * @param type The type of docking station.
   * @param pos  The block position pos.
   * @param side The side of the block.
   */
  public DockingStation(DockingStationType<T> type, BlockPos pos, Direction side) {
    this.type = type;
    this.pos = pos;
    this.side = side;
  }

  /**
   * Default constructor for NBT loading.
   */
  public DockingStation(DockingStationType<T> iType) {
    type = iType;
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
    return pos.getX();
  }

  /**
   * Returns the Y coordinate of this station.
   *
   * @return The Y coordinate.
   */
  public int y() {
    return pos.getY();
  }

  /**
   * Returns the Z coordinate of this station.
   *
   * @return The Z coordinate.
   */
  public int z() {
    return pos.getZ();
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
  public @Nullable RobotEntityBase robotTaking() {
    if (robotTakingId == RobotEntityBase.NULL_ROBOT_ID) {
      return null;
    } else if (robotTaking == null) {
      robotTaking = RobotManager.registry().getRegistry(world).getLoadedRobot(
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
      IRobotRegistry registry = RobotManager.registry().getRegistry(world);
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
      IRobotRegistry registry = RobotManager.registry().getRegistry(world);
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
      IRobotRegistry registry = RobotManager.registry().getRegistry(world);
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
    if (robotTaking == robot || robotTakingId == robot.getRobotId()) {
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
  @Override
  public void writeTag(CompoundTag nbt) {
    NbtWriter.of(nbt)
      .putLong("Pos", pos.asLong())
      .putDirection("side", side)
      .putBoolean("isMain", linkIsMain)
      .putLong("robotId", robotTakingId)
      .done();
  }

  /**
   * Reads the station's state from NBT.
   *
   * @param nbt The NBT tag to read from.
   */
  @Override
  public void readTag(CompoundTag nbt) {
    NbtReader.of(nbt)
      .applyLong("Pos", pos -> this.pos = BlockPos.of(pos))
      .applyDirection("side", side -> this.side = side)
      .applyBoolean("isMain", linkIsMain -> this.linkIsMain = linkIsMain)
      .applyLong("robotId", robotTakingId -> this.robotTakingId = robotTakingId)
      .done();
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
   * Returns the block pos (position) of this station.
   *
   * @return The block pos.
   */
  public BlockPos pos() {
    return pos;
  }

  @Override
  public String toString() {
    return "{" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ", " + side + " :" + robotTakingId
      + "}";
  }

  /**
   * Checks if the linked robot is currently docked at this station.
   *
   * @return {@code true} if docked, {@code false} otherwise.
   */
  public boolean linkIsDocked() {
    var taking = robotTaking();
    if (taking != null) {
      return taking.getDockingStation() == this;
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
  @Nullable
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
  @Nullable
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
  @Nullable
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
  @Nullable
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
  @Nullable
  public IRequestProvider getRequestProvider() {
    return null;
  }

  /**
   * Called when the chunk containing this station is unloaded.
   */
  public void onChunkUnload() {
  }

  public DockingStationType<T> getType() {
    return type;
  }
}
