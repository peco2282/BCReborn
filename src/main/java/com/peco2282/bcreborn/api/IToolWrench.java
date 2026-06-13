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
package com.peco2282.bcreborn.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;


/**
 * Implement this interface on {@link net.minecraft.world.item.Item} subclasses
 * to allow them to function as a wrench in BuildCraft.
 */
public interface IToolWrench {

  /**
   * Checks if the wrench can be used at the specified coordinates.
   *
   * @param player The player attempting to use the wrench.
   * @param x      The x-coordinate of the block.
   * @param y      The y-coordinate of the block.
   * @param z      The z-coordinate of the block.
   * @return True if wrenching is allowed.
   */
  boolean canWrench(Player player, int x, int y, int z);

  /**
   * Called after the wrench has been successfully used.
   * Use this to decrease durability or perform other post-use actions.
   *
   * @param player The player who used the wrench.
   * @param x      The x-coordinate of the block.
   * @param y      The y-coordinate of the block.
   * @param z      The z-coordinate of the block.
   */
  void wrenchUsed(Player player, int x, int y, int z);

  /**
   * Checks if the wrench can be used at the specified position.
   *
   * @param player The player attempting to use the wrench.
   * @param pos    The position of the block.
   * @return True if wrenching is allowed.
   */
  default boolean canWrench(Player player, BlockPos pos) {
    return canWrench(player, pos.getX(), pos.getY(), pos.getZ());
  }

  /**
   * Called after the wrench has been successfully used at the specified position.
   *
   * @param player The player who used the wrench.
   * @param pos    The position of the block.
   */
  default void wrenchUsed(Player player, BlockPos pos) {
    wrenchUsed(player, pos.getX(), pos.getY(), pos.getZ());
  }
}
