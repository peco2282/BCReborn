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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;

/**
 * Interface for the registry that tracks robots and docking stations in a world.
 */
public interface IRobotRegistry {
  /**
   * Generates and returns the next unique robot ID.
   *
   * @return The next robot ID.
   */
  long getNextRobotId();

  /**
   * Registers a robot entity in the registry.
   *
   * @param robot The robot entity.
   */
  void registerRobot(RobotEntityBase robot);

  /**
   * Removes a robot entity from the registry (e.g., when it is destroyed).
   *
   * @param robot The robot entity.
   */
  void killRobot(RobotEntityBase robot);

  /**
   * Notifies the registry that a robot entity is being unloaded.
   *
   * @param robot The robot entity.
   */
  void unloadRobot(RobotEntityBase robot);

  /**
   * Returns a loaded robot entity by its ID.
   *
   * @param id The robot ID.
   * @return The loaded robot entity, or {@code null} if not found.
   */
  RobotEntityBase getLoadedRobot(long id);

  /**
   * Checks if a resource is currently taken by a robot.
   *
   * @param resourceId The resource identifier.
   * @return {@code true} if taken, {@code false} otherwise.
   */
  boolean isTaken(ResourceId resourceId);

  /**
   * Returns the ID of the robot currently taking the specified resource.
   *
   * @param resourceId The resource identifier.
   * @return The robot ID.
   */
  long robotIdTaking(ResourceId resourceId);

  /**
   * Returns the robot entity currently taking the specified resource.
   *
   * @param resourceId The resource identifier.
   * @return The robot entity, or {@code null} if none.
   */
  RobotEntityBase robotTaking(ResourceId resourceId);

  /**
   * Attempts to take a resource for a robot.
   *
   * @param resourceId The resource identifier.
   * @param robot      The robot object.
   * @return {@code true} if successful, {@code false} otherwise.
   */
  boolean take(ResourceId resourceId, Object robot);

  /**
   * Attempts to take a resource for a robot by its ID.
   *
   * @param resourceId The resource identifier.
   * @param robotId    The robot ID.
   * @return {@code true} if successful, {@code false} otherwise.
   */
  boolean take(ResourceId resourceId, long robotId);

  /**
   * Releases a resource from the robot that has taken it.
   *
   * @param resourceId The resource identifier.
   */
  void release(ResourceId resourceId);

  /**
   * Releases all resources taken by the specified robot.
   *
   * @param robot The robot object.
   */
  void releaseResources(Object robot);

  /**
   * Returns the docking station at the specified position and side.
   *
   * @param pos  The block position.
   * @param side The side.
   * @return The docking station, or {@code null} if none.
   */
  DockingStation getStation(BlockPos pos, Direction side);

  /**
   * Returns all registered docking stations.
   *
   * @return A collection of docking stations.
   */
  Collection<DockingStation> getStations();

  /**
   * Registers a docking station.
   *
   * @param station The docking station.
   */
  void registerStation(DockingStation station);

  /**
   * Removes a docking station from the registry.
   *
   * @param station The docking station.
   */
  void removeStation(DockingStation station);

  /**
   * Marks a docking station as taken by a robot.
   *
   * @param station The docking station.
   * @param robotId The robot ID.
   */
  void take(DockingStation station, long robotId);

  /**
   * Marks a docking station as released by a robot.
   *
   * @param station The docking station.
   * @param robotId The robot ID.
   */
  void release(DockingStation station, long robotId);

  /**
   * Writes the registry state to NBT.
   *
   * @param nbt The NBT tag.
   */
  void writeToNBT(CompoundTag nbt);

  /**
   * Reads the registry state from NBT.
   *
   * @param nbt The NBT tag.
   */
  void readFromNBT(CompoundTag nbt);

  /**
   * Marks the registry as dirty, indicating that it needs to be saved.
   */
  void registryMarkDirty();
}
