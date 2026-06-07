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
package com.peco2282.bcreborn.robotics.registry;

import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.IRobotRegistryProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;


public class RobotRegistryProvider implements IRobotRegistryProvider {
  private static final HashMap<ResourceKey<Level>, RobotRegistry> registries = new HashMap<>();

  @Override
  public synchronized RobotRegistry getRegistry(Level world) {
    if (!(world instanceof ServerLevel serverLevel)) {
      return null; // Registry only exists on server
    }

    if (!registries.containsKey(world.dimension())
      || registries.get(world.dimension()).level != world) {

      RobotRegistry newRegistry = serverLevel.getDataStorage().computeIfAbsent(RobotRegistry::load, RobotRegistry::new, "robotRegistry");

      newRegistry.level = serverLevel;

      for (DockingStation d : newRegistry.stations.values()) {
        d.world = world;
      }

      MinecraftForge.EVENT_BUS.register(newRegistry);

      registries.put(world.dimension(), newRegistry);

      return newRegistry;
    }

    return registries.get(world.dimension());
  }
}
