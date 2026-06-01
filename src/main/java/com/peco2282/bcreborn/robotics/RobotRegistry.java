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
package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.api.robots.*;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.security.InvalidParameterException;
import java.util.*;

public class RobotRegistry extends SavedData implements IRobotRegistry {

  protected final HashMap<StationIndex, DockingStation> stations = new HashMap<>();
  private final Long2ObjectOpenHashMap<RobotEntity> robotsLoaded = new Long2ObjectOpenHashMap<>();
  private final HashSet<RobotEntity> robotsLoadedSet = new HashSet<>();
  private final HashMap<ResourceId, Long> resourcesTaken = new HashMap<>();
  private final Long2ObjectOpenHashMap<HashSet<ResourceId>> resourcesTakenByRobot = new Long2ObjectOpenHashMap<>();
  private final Long2ObjectOpenHashMap<HashSet<StationIndex>> stationsTakenByRobot = new Long2ObjectOpenHashMap<>();
  protected Level level;
  private long nextRobotID = Long.MIN_VALUE;

  public RobotRegistry() {
  }

  public static RobotRegistry load(CompoundTag nbt) {
    RobotRegistry registry = new RobotRegistry();
    registry.readFromNBT(nbt);
    return registry;
  }

  @Override
  public long getNextRobotId() {
    long result = nextRobotID;
    nextRobotID = nextRobotID + 1;
    setDirty();
    return result;
  }

  @Override
  public void registerRobot(RobotEntityBase robotObj) {
    if (!(robotObj instanceof RobotEntity robot)) return;
    setDirty();

    if (robot.getRobotId() == RobotEntityBase.NULL_ROBOT_ID) {
      robot.setUniqueRobotId(getNextRobotId());
    }
    addRobotLoaded(robot);
  }

  private HashSet<ResourceId> getResourcesTakenByRobot(long robotId) {
    return resourcesTakenByRobot.get(robotId);
  }

  private HashSet<StationIndex> getStationsTakenByRobot(long robotId) {
    return stationsTakenByRobot.get(robotId);
  }

  private void addRobotLoaded(RobotEntity robot) {
    robotsLoaded.put(robot.getRobotId(), robot);
    robotsLoadedSet.add(robot);
  }

  private void removeRobotLoaded(RobotEntity robot) {
    robotsLoaded.remove(robot.getRobotId());
    robotsLoadedSet.remove(robot);
  }

  @Override
  public void killRobot(RobotEntityBase robot) {
    setDirty();
    releaseResources(robot);
    if (robot instanceof RobotEntity entityRobot) {
      removeRobotLoaded(entityRobot);
    }
  }

  @Override
  public void unloadRobot(RobotEntityBase robot) {
    setDirty();
    releaseResources(robot);
    if (robot instanceof RobotEntity entityRobot) {
      removeRobotLoaded(entityRobot);
    }
  }

  @Override
  public RobotEntityBase getLoadedRobot(long id) {
    return robotsLoaded.get(id);
  }

  @Override
  public synchronized boolean isTaken(ResourceId resourceId) {
    return robotIdTaking(resourceId) != RobotEntityBase.NULL_ROBOT_ID;
  }

  @Override
  public synchronized long robotIdTaking(ResourceId resourceId) {
    if (!resourcesTaken.containsKey(resourceId)) {
      return RobotEntityBase.NULL_ROBOT_ID;
    }

    long robotId = resourcesTaken.get(resourceId);
    RobotEntity robot = robotsLoaded.get(robotId);

    if (robot != null && !robot.isRemoved()) {
      return robotId;
    } else {
      release(resourceId);
      return RobotEntityBase.NULL_ROBOT_ID;
    }
  }

  @Override
  public synchronized RobotEntityBase robotTaking(ResourceId resourceId) {
    long robotId = robotIdTaking(resourceId);
    if (robotId == RobotEntityBase.NULL_ROBOT_ID) {
      return null;
    }
    return robotsLoaded.get(robotId);
  }

  @Override
  public synchronized boolean take(ResourceId resourceId, Object robot) {
    if (robot instanceof RobotEntityBase robotBase) {
      return take(resourceId, robotBase.getRobotId());
    }
    return false;
  }

  @Override
  public synchronized boolean take(ResourceId resourceId, long robotId) {
    if (resourceId == null) {
      return false;
    }
    setDirty();
    if (!resourcesTaken.containsKey(resourceId)) {
      resourcesTaken.put(resourceId, robotId);
      resourcesTakenByRobot.computeIfAbsent(robotId, k -> new HashSet<>()).add(resourceId);
      return true;
    }
    return false;
  }

  @Override
  public synchronized void release(ResourceId resourceId) {
    if (resourceId == null) return;
    setDirty();
    Long robotId = resourcesTaken.remove(resourceId);
    if (robotId != null) {
      HashSet<ResourceId> taken = getResourcesTakenByRobot(robotId);
      if (taken != null) {
        taken.remove(resourceId);
      }
    }
  }

  @Override
  public synchronized void releaseResources(Object robotObj) {
    if (!(robotObj instanceof RobotEntityBase robot)) return;
    setDirty();

    long robotId = robot.getRobotId();
    HashSet<ResourceId> resourceSet = resourcesTakenByRobot.remove(robotId);
    if (resourceSet != null) {
      for (ResourceId id : new HashSet<>(resourceSet)) {
        release(id);
      }
    }

    HashSet<StationIndex> stationSet = stationsTakenByRobot.remove(robotId);
    if (stationSet != null) {
      for (StationIndex s : stationSet) {
        DockingStation d = stations.get(s);
        if (d != null) {
          // Logic for releasing station if applicable
        }
      }
    }
  }

  @Override
  public synchronized DockingStation getStation(BlockPos pos, Direction side) {
    return stations.get(new StationIndex(side, pos.getX(), pos.getY(), pos.getZ()));
  }

  @Override
  public synchronized Collection<DockingStation> getStations() {
    return stations.values();
  }

  @Override
  public synchronized void registerStation(DockingStation station) {
    setDirty();
    StationIndex index = new StationIndex(station);
    if (stations.containsKey(index)) {
      throw new InvalidParameterException("Station " + index + " already registered");
    }
    stations.put(index, station);
  }

  @Override
  public synchronized void removeStation(DockingStation station) {
    setDirty();
    StationIndex index = new StationIndex(station);
    stations.remove(index);
  }

  @Override
  public synchronized void take(DockingStation station, long robotId) {
    setDirty();
    stationsTakenByRobot.computeIfAbsent(robotId, k -> new HashSet<>()).add(new StationIndex(station));
  }

  @Override
  public synchronized void release(DockingStation station, long robotId) {
    setDirty();
    HashSet<StationIndex> taken = stationsTakenByRobot.get(robotId);
    if (taken != null) {
      taken.remove(new StationIndex(station));
    }
  }

  @Override
  public CompoundTag save(CompoundTag nbt) {
    nbt.putLong("nextRobotID", nextRobotID);
    ListTag resourceList = new ListTag();
    for (Map.Entry<ResourceId, Long> e : resourcesTaken.entrySet()) {
      CompoundTag cpt = new CompoundTag();
      CompoundTag resourceId = new CompoundTag();
      e.getKey().writeToNBT(resourceId);
      cpt.put("resourceId", resourceId);
      cpt.putLong("robotId", e.getValue());
      resourceList.add(cpt);
    }
    nbt.put("resourceList", resourceList);

    ListTag stationList = new ListTag();
    for (DockingStation station : stations.values()) {
      CompoundTag cpt = new CompoundTag();
      station.writeToNBT(cpt);
      String name = RobotManager.getDockingStationName(station.getClass());
      if (name != null) {
        cpt.putString("stationType", name);
        stationList.add(cpt);
      }
    }
    nbt.put("stationList", stationList);
    return nbt;
  }

  @Override
  public void writeToNBT(CompoundTag nbt) {
    save(nbt);
  }

  @Override
  public void readFromNBT(CompoundTag nbt) {
    nextRobotID = nbt.getLong("nextRobotID");
    ListTag resourceList = nbt.getList("resourceList", 10);
    for (int i = 0; i < resourceList.size(); ++i) {
      CompoundTag cpt = resourceList.getCompound(i);
      ResourceId resourceId = ResourceId.load(cpt.getCompound("resourceId"));
      long robotId = cpt.getLong("robotId");
      take(resourceId, robotId);
    }

    ListTag stationList = nbt.getList("stationList", 10);
    for (int i = 0; i < stationList.size(); ++i) {
      CompoundTag cpt = stationList.getCompound(i);
      String type = cpt.getString("stationType");
      Class<? extends DockingStation> cls = RobotManager.getDockingStationByName(type);
      if (cls == null && type.isEmpty()) cls = DockingStationPipe.class;

      if (cls != null) {
        try {
          DockingStation station = cls.getDeclaredConstructor().newInstance();
          station.readFromNBT(cpt);
          registerStation(station);
        } catch (Exception e) {
          BCLog.logger.error("Could not load docking station", e);
        }
      }
    }
  }

  @Override
  public void registryMarkDirty() {
    setDirty();
  }

  @SubscribeEvent
  public void onChunkUnload(ChunkEvent.Unload e) {
    if (e.getLevel() == this.level) {
      for (DockingStation station : new ArrayList<>(stations.values())) {
        if (!level.isLoaded(station.index().toBlockPos())) {
          station.onChunkUnload();
        }
      }
    }
  }
}
