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
package com.peco2282.bcreborn.api.core;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class BuildCraftAPI {

  public static final Set<Block> softBlocks = new HashSet<Block>();
  public static final HashMap<String, IWorldProperty> worldProperties = new HashMap<String, IWorldProperty>();
  public static ICoreProxy proxy;

  /**
   * Deactivate constructor
   */
  private BuildCraftAPI() {
  }

  public static String getVersion() {
    try {
      Class<?> clazz = Class.forName("buildcraft.core.Version");
      Method method = clazz.getDeclaredMethod("getVersion");
      return String.valueOf(method.invoke(null));
    } catch (Exception e) {
      return "UNKNOWN VERSION";
    }
  }

  public static IWorldProperty getWorldProperty(String name) {
    return worldProperties.get(name);
  }

  public static void registerWorldProperty(String name, IWorldProperty property) {
    if (worldProperties.containsKey(name)) {
      BCLog.logger.warn("The WorldProperty key '" + name + "' is being overidden with " + property.getClass().getSimpleName() + "!");
    }
    worldProperties.put(name, property);
  }

  public static boolean isSoftBlock(Level world, BlockPos pos) {
    IWorldProperty soft = worldProperties.get("soft");
    return soft != null && soft.get(world, pos);
  }

  public static boolean isSoftBlock(Level world, int x, int y, int z) {
    return isSoftBlock(world, new BlockPos(x, y, z));
  }
}
