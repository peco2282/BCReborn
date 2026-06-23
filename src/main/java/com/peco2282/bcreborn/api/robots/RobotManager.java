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

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Global manager for robot-related registrations.
 * <p>
 * This class handles the registration of robot AIs, resource IDs, and docking station types.
 */
@SuppressWarnings("unchecked")
public abstract class RobotManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(RobotManager.class);

  private static final Map<ResourceLocation, AIRobotType<?>> robotTypes = new HashMap<>();
  private static final Map<ResourceLocation, ResourceIdType<?>> resourceIdTypes = new HashMap<>();
  private static final Map<ResourceLocation, DockingStationType<?>> dockingStationTypes = new HashMap<>();

  /**
   * Provider for the robot registry.
   */
  private static IRobotRegistryProvider registryProvider;

  public static IRobotRegistryProvider registry() {
    return registryProvider;
  }

  @ApiStatus.Internal
  public static void registry(IRobotRegistryProvider provider) {
    registryProvider = provider;
  }

  private static AIRobotType<? extends RedstoneBoardRobot<?>> emptyBoard;

  public static void setEmpty(AIRobotType<? extends RedstoneBoardRobot<?>> empty) {
    emptyBoard = empty;
  }

  // Robot types
  @Contract(value = "_,_ -> new")
  public static <T extends AIRobot<T>> AIRobotType<T> registerRobotType(ResourceLocation id, Function<RobotEntityBase, @NotNull T> type) {
    return registerRobotType(new AIRobotType<>(id, type));
  }

  @Contract("_ -> param1")
  public static <T extends AIRobot<T>> AIRobotType<T> registerRobotType(AIRobotType<T> type) {
    var id = type.id();
    if (robotTypes.containsKey(id)) {
      LOGGER.info("Overriding Robot {} with {}", robotTypes.get(id).id(), type.id());
    }
    robotTypes.put(id, type);
    return type;
  }

  public static boolean hasRobotType(String id) {
    return robotTypes.containsKey(ResourceLocation.parse(id));
  }

  @Contract(pure = true)
  public static boolean hasRobotType(ResourceLocation id) {
    return robotTypes.containsKey(id);
  }

  @Nullable
  public static <T extends AIRobot<T>> AIRobotType<T> getRobotType(String id) {
    return getRobotType(ResourceLocation.parse(id));
  }

  @Contract(pure = true)
  @Nullable
  public static <T extends AIRobot<T>> AIRobotType<T> getRobotType(ResourceLocation id) {
    return (AIRobotType<T>) robotTypes.get(id);
  }

  public static <T extends AIRobot<T>> T createRobot(AIRobotType<T> type, RobotEntityBase base) {
    return type.create(base);
  }

  public static <T extends AIRobot<T>> T createRobot(String id, RobotEntityBase base) {
    return createRobot(ResourceLocation.parse(id), base);
  }

  public static <T extends AIRobot<T>> T createRobot(ResourceLocation id, RobotEntityBase base) {
    var type = robotTypes.get(id);
    if (type == null) {
      throw new IllegalArgumentException("Unknown robot type: " + id);
    }
    return (T) type.create(base);
  }

  public static RedstoneBoardRobot<?> createEmptyRobot(RobotEntityBase base) {
    //noinspection RedundantCast
    return (RedstoneBoardRobot<?>) createRobot(emptyBoard.id(), base);
  }

  public static <T extends AIRobot<T>> T createRobot(String id, RobotEntityBase base, CompoundTag init) {
    return createRobot(ResourceLocation.parse(id), base, init);
  }

  public static <T extends AIRobot<T>> T createRobot(ResourceLocation id, RobotEntityBase base, CompoundTag init) {
    var robot = createRobot(id, base);
    robot.loadFromNBT(init);

    return (T) robot;
  }

  // Resource ID types
  public static <T extends ResourceId<T>> ResourceIdType<T> registerResourceIdType(ResourceLocation id, Supplier<T> type) {
    return registerResourceIdType(new ResourceIdType<>(id, type));
  }

  @Contract("_ -> param1")
  public static <T extends ResourceId<T>> ResourceIdType<T> registerResourceIdType(ResourceIdType<T> type) {
    var id = type.id();
    if (resourceIdTypes.containsKey(id)) {
      LOGGER.info("Overriding Resource ID {} with {}", resourceIdTypes.get(id).id(), type.id());
    }
    resourceIdTypes.put(id, type);
    return type;
  }

  @Contract(pure = true)
  @Nullable
  public static ResourceIdType<?> getResourceIdType(String id) {
    return getResourceIdType(ResourceLocation.parse(id));
  }

  @Nullable
  public static <T extends ResourceId<T>> ResourceIdType<T> getResourceIdType(ResourceLocation id) {
    return (ResourceIdType<T>) resourceIdTypes.get(id);
  }

  public static <T extends ResourceId<T>> T createResourceId(ResourceIdType<T> type) {
    return type.create();
  }

  public static <T extends ResourceId<T>> T createResourceId(String id) {
    return createResourceId(ResourceLocation.parse(id));
  }

  public static <T extends ResourceId<T>> T createResourceId(String id, CompoundTag nbt) {
    return createResourceId(ResourceLocation.parse(id), nbt);
  }

  public static <T extends ResourceId<T>> T createResourceId(ResourceLocation id) {
    var type = getResourceIdType(id);
    if (type == null) {
      throw new IllegalArgumentException("Unknown resource ID type: " + id);
    }
    return (T) type.create();
  }

  public static <T extends ResourceId<T>> T createResourceId(ResourceLocation id, CompoundTag nbt) {
    T res = createResourceId(id);
    res.readFromNBT(nbt);
    return res;
  }

  public static <T extends ResourceId<T>> T createResourceId(ResourceIdType<T> type, CompoundTag nbt) {
    var res = type.create();
    res.readFromNBT(nbt);
    return res;
  }

  public static boolean hasResourceIdType(String id) {
    return hasResourceIdType(ResourceLocation.parse(id));
  }

  @Contract(pure = true)
  public static boolean hasResourceIdType(ResourceLocation id) {
    return resourceIdTypes.containsKey(id);
  }

  // Docking station types
  public static <T extends DockingStation<T>> DockingStationType<T> registerDockingStationType(ResourceLocation id, Supplier<T> type) {
    return registerDockingStationType(new DockingStationType<>(id, type));
  }

  @Contract("_ -> param1")
  public static <T extends DockingStation<T>> DockingStationType<T> registerDockingStationType(DockingStationType<T> type) {
    var id = type.id();
    if (dockingStationTypes.containsKey(id)) {
      LOGGER.info("Overriding Docking Station ID {} with {}", dockingStationTypes.get(id).id(), type.id());
    }
    dockingStationTypes.put(id, type);
    return type;
  }

  @Nullable
  public static <T extends DockingStation<T>> DockingStationType<T> getDockingStationType(String id) {
    return getDockingStationType(ResourceLocation.parse(id));
  }

  @Nullable
  public static <T extends DockingStation<T>> DockingStationType<T> getDockingStationType(ResourceLocation id) {
    return (DockingStationType<T>) dockingStationTypes.get(id);
  }

  public static <T extends DockingStation<T>> T createDockingStation(DockingStationType<T> type) {
    return type.create();
  }

  public static <T extends DockingStation<T>> T createDockingStation(String id) {
    return createDockingStation(ResourceLocation.parse(id));
  }

  public static <T extends DockingStation<T>> T createDockingStation(ResourceLocation id) {
    var station = getDockingStationType(id);
    if (station == null) {
      throw new IllegalArgumentException("Unknown docking station ID: " + id);
    }
    return (T) station.create();
  }

  public static <T extends DockingStation<T>> T createDockingStation(String id, CompoundTag tag) {
    return createDockingStation(ResourceLocation.parse(id), tag);
  }

  public static <T extends DockingStation<T>> T createDockingStation(ResourceLocation id, CompoundTag tag) {
    var station = (T) createDockingStation(id);
    station.readFromNBT(tag);
    return station;
  }

  public static boolean hasDockingStationType(String id) {
    return hasDockingStationType(ResourceLocation.parse(id));
  }

  public static boolean hasDockingStationType(ResourceLocation id) {
    return dockingStationTypes.containsKey(id);
  }
}
