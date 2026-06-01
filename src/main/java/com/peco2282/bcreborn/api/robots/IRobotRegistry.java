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

public interface IRobotRegistry {
  long getNextRobotId();

  void registerRobot(RobotEntityBase robot);

  void killRobot(RobotEntityBase robot);

  void unloadRobot(RobotEntityBase robot);

  RobotEntityBase getLoadedRobot(long id);

  boolean isTaken(ResourceId resourceId);

  long robotIdTaking(ResourceId resourceId);

  RobotEntityBase robotTaking(ResourceId resourceId);

  boolean take(ResourceId resourceId, Object robot);

  boolean take(ResourceId resourceId, long robotId);

  void release(ResourceId resourceId);

  void releaseResources(Object robot);

  DockingStation getStation(BlockPos pos, Direction side);

  Collection<DockingStation> getStations();

  void registerStation(DockingStation station);

  void removeStation(DockingStation station);

  void take(DockingStation station, long robotId);

  void release(DockingStation station, long robotId);

  void writeToNBT(CompoundTag nbt);

  void readFromNBT(CompoundTag nbt);

  void registryMarkDirty();
}
