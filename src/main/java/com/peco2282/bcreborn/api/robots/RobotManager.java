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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Global manager for robot-related registrations.
 * <p>
 * This class handles the registration of robot AIs, resource IDs, and docking station types.
 */
public abstract class RobotManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(RobotManager.class);
  private static final Map<Class<? extends AIRobot>, String> aiRobotsNames;
  private static final Map<String, Class<? extends AIRobot>> aiRobotsByNames;
  private static final Map<String, Class<? extends AIRobot>> aiRobotsByLegacyClassNames;
  private static final Map<Class<? extends ResourceId>, String> resourceIdNames;
  private static final Map<String, Class<? extends ResourceId>> resourceIdByNames;
  private static final Map<String, Class<? extends ResourceId>> resourceIdLegacyClassNames;
  private static final Map<Class<? extends DockingStation>, String> dockingStationNames;
  private static final Map<String, Class<? extends DockingStation>> dockingStationByNames;
  /**
   * Provider for the robot registry.
   */
  public static IRobotRegistryProvider registryProvider;

  /**
   * List of registered AIRobot classes.
   */
  public static ArrayList<Class<? extends AIRobot>> aiRobots = new ArrayList<>();

  static {
    aiRobotsByNames = new HashMap<>();
    aiRobotsNames = new HashMap<>();
    aiRobotsByLegacyClassNames = new HashMap<>();
    resourceIdNames = new HashMap<>();
    resourceIdByNames = new HashMap<>();
    resourceIdLegacyClassNames = new HashMap<>();
    dockingStationNames = new HashMap<>();
    dockingStationByNames = new HashMap<>();
    registerResourceId(ResourceIdBlock.class, "resourceIdBlock", "buildcraft.core.robots.ResourceIdBlock");
    registerResourceId(ResourceIdRequest.class, "resourceIdRequest", "buildcraft.core.robots.ResourceIdRequest");
  }

  /**
   * Registers a robot AI class.
   *
   * @param aiRobot The AI class to register.
   * @param name    The unique name for this AI.
   */
  public static void registerAIRobot(Class<? extends AIRobot> aiRobot, String name) {
    registerAIRobot(aiRobot, name, null);
  }

  /**
   * Registers a robot AI class with an optional legacy class name for backward compatibility.
   *
   * @param aiRobot         The AI class to register.
   * @param name            The unique name for this AI.
   * @param legacyClassName The legacy class name (can be {@code null}).
   */
  public static void registerAIRobot(Class<? extends AIRobot> aiRobot, String name, String legacyClassName) {
    if (aiRobotsByNames.containsKey(name)) {
      LOGGER.info("Overriding {} with {}", aiRobotsByNames.get(name).getName(), aiRobot.getName());
    }
    try {
      aiRobot.getConstructor(RobotEntityBase.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("AI class " + aiRobot.getName() + " lacks NBT load constructor! This is a bug!");
    }
    aiRobots.add(aiRobot);
    aiRobotsByNames.put(name, aiRobot);
    aiRobotsNames.put(aiRobot, name);
    if (legacyClassName != null) {
      aiRobotsByLegacyClassNames.put(legacyClassName, aiRobot);
    }
  }

  /**
   * Returns the AI robot class associated with the given name.
   *
   * @param aiRobotName The name of the AI.
   * @return The AI class, or {@code null} if not found.
   */
  public static Class<?> getAIRobotByName(String aiRobotName) {
    return aiRobotsByNames.get(aiRobotName);
  }

  /**
   * Returns the name associated with the given AI robot class.
   *
   * @param aiRobotClass The AI class.
   * @return The name, or {@code null} if not found.
   */
  public static String getAIRobotName(Class<? extends AIRobot> aiRobotClass) {
    return aiRobotsNames.get(aiRobotClass);
  }

  /**
   * Returns the AI robot class associated with the given legacy class name.
   *
   * @param aiRobotLegacyClassName The legacy class name.
   * @return The AI class, or {@code null} if not found.
   */
  public static Class<?> getAIRobotByLegacyClassName(String aiRobotLegacyClassName) {
    return aiRobotsByLegacyClassNames.get(aiRobotLegacyClassName);
  }

  /**
   * Registers a resource ID class.
   *
   * @param resourceId The resource ID class.
   * @param name       The unique name for this resource ID.
   */
  public static void registerResourceId(Class<? extends ResourceId> resourceId, String name) {
    registerResourceId(resourceId, name, null);
  }

  /**
   * Registers a resource ID class with an optional legacy class name.
   *
   * @param resourceId      The resource ID class.
   * @param name            The unique name for this resource ID.
   * @param legacyClassName The legacy class name (can be {@code null}).
   */
  public static void registerResourceId(Class<? extends ResourceId> resourceId, String name, String legacyClassName) {
    resourceIdByNames.put(name, resourceId);
    resourceIdNames.put(resourceId, name);
    if (legacyClassName != null) {
      resourceIdLegacyClassNames.put(legacyClassName, resourceId);
    }
  }

  /**
   * Returns the resource ID class associated with the given name.
   *
   * @param resourceIdName The name of the resource ID.
   * @return The resource ID class, or {@code null} if not found.
   */
  public static Class<?> getResourceIdByName(String resourceIdName) {
    return resourceIdByNames.get(resourceIdName);
  }

  /**
   * Returns the name associated with the given resource ID class.
   *
   * @param resourceIdClass The resource ID class.
   * @return The name, or {@code null} if not found.
   */
  public static String getResourceIdName(Class<? extends ResourceId> resourceIdClass) {
    return resourceIdNames.get(resourceIdClass);
  }

  /**
   * Returns the resource ID class associated with the given legacy class name.
   *
   * @param resourceIdLegacyClassName The legacy class name.
   * @return The resource ID class, or {@code null} if not found.
   */
  public static Class<?> getResourceIdByLegacyClassName(String resourceIdLegacyClassName) {
    return resourceIdLegacyClassNames.get(resourceIdLegacyClassName);
  }

  /**
   * Registers a docking station class.
   *
   * @param dockingStation The docking station class.
   * @param name           The unique name for this docking station type.
   */
  public static void registerDockingStation(Class<? extends DockingStation> dockingStation, String name) {
    dockingStationByNames.put(name, dockingStation);
    dockingStationNames.put(dockingStation, name);
  }

  /**
   * Returns the docking station class associated with the given name.
   *
   * @param dockingStationTypeName The name of the docking station type.
   * @return The docking station class, or {@code null} if not found.
   */
  public static Class<? extends DockingStation> getDockingStationByName(String dockingStationTypeName) {
    return dockingStationByNames.get(dockingStationTypeName);
  }

  /**
   * Returns the name associated with the given docking station class.
   *
   * @param dockingStation The docking station class.
   * @return The name, or {@code null} if not found.
   */
  public static String getDockingStationName(Class<? extends DockingStation> dockingStation) {
    return dockingStationNames.get(dockingStation);
  }
}
