/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.robots;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;

public interface IRobotRegistry {
    long getNextRobotId();

    void registerRobot(Object robot);

    void killRobot(Object robot);

    void unloadRobot(Object robot);

    Object getLoadedRobot(long id);

    boolean isTaken(ResourceId resourceId);

    long robotIdTaking(ResourceId resourceId);

    Object robotTaking(ResourceId resourceId);

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
