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
import net.minecraft.world.item.Item;


/**
 * Implement this interface on {@link Item} subclasses
 * to allow them to function as a wrench in BuildCraft.
 */
public interface IToolWrench {
  /**
   * Checks if the wrench can be used at the specified position.
   *
   * @param player The player attempting to use the wrench.
   * @param pos    The position of the block.
   * @return True if wrenching is allowed.
   */
  boolean canWrench(Player player, BlockPos pos);

  /**
   * Called after the wrench has been successfully used at the specified position.
   *
   * @param player The player who used the wrench.
   * @param pos    The position of the block.
   */
  void wrenchUsed(Player player, BlockPos pos);
}
