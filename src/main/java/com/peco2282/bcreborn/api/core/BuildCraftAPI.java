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


import com.peco2282.bcreborn.api.events.BCEventBus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Main entry point for the BuildCraft API.
 * Provides access to global registries and utility methods.
 */
public final class BuildCraftAPI {
  /**
   * Set of blocks that are considered "soft" (easily breakable by machines).
   */
  public static final Set<Block> softBlocks = new HashSet<>();
  /**
   * Map of world properties, used to query various states of the world at specific positions.
   */
  public static final HashMap<String, IWorldProperty> worldProperties = new HashMap<>();
  private static BCEventBus bus;

  /**
   * Private constructor to prevent instantiation.
   */
  private BuildCraftAPI() {
  }

  public static BCEventBus bus() {
    return bus;
  }

  @ApiStatus.Internal
  public static void bus(BCEventBus bus) {
    BuildCraftAPI.bus = bus;
  }

  /**
   * Gets a world property by name.
   *
   * @param name The name of the property.
   * @return The {@link IWorldProperty}, or null if not found.
   */
  public static IWorldProperty getWorldProperty(String name) {
    return worldProperties.get(name);
  }

  /**
   * Registers a world property.
   *
   * @param name     The name of the property.
   * @param property The {@link IWorldProperty} implementation.
   */
  public static void registerWorldProperty(String name, IWorldProperty property) {
    if (worldProperties.containsKey(name)) {
      BCLog.logger.warn("The WorldProperty key '" + name + "' is being overidden with " + property.getClass().getSimpleName() + "!");
    }
    worldProperties.put(name, property);
  }

  /**
   * Checks if a block at the given position is "soft".
   *
   * @param world The world.
   * @param pos   The position.
   * @return True if it is a soft block.
   */
  public static boolean isSoftBlock(Level world, BlockPos pos) {
    IWorldProperty soft = worldProperties.get("soft");
    return soft != null && soft.get(world, pos);
  }

  /**
   * Checks if a block at the given coordinates is "soft".
   *
   * @param world The world.
   * @param x     The x-coordinate.
   * @param y     The y-coordinate.
   * @param z     The z-coordinate.
   * @return True if it is a soft block.
   */
  public static boolean isSoftBlock(Level world, int x, int y, int z) {
    return isSoftBlock(world, new BlockPos(x, y, z));
  }
}
