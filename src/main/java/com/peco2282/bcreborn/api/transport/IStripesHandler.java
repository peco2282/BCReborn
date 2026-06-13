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
package com.peco2282.bcreborn.api.transport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Interface for handlers that define behavior when a Stripes Pipe interacts with the world.
 */
public interface IStripesHandler {
  /**
   * Gets the type of the stripes handler.
   *
   * @return The {@link StripesHandlerType}.
   */
  StripesHandlerType getType();

  /**
   * Checks if this handler should handle the given {@link ItemStack}.
   *
   * @param stack The stack to check.
   * @return True if this handler can process the stack.
   */
  boolean shouldHandle(ItemStack stack);

  /**
   * Performs the action associated with this handler.
   *
   * @param world     The world.
   * @param pos       The target position.
   * @param direction The direction of interaction.
   * @param stack     The item stack being used.
   * @param player    The player instance (may be a fake player).
   * @param activator The stripes activator.
   * @return True if the action was successful.
   */
  boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator);

  /**
   * Represents the type of interaction for the stripes handler.
   */
  enum StripesHandlerType {
    ITEM_USE,
    BLOCK_BREAK
  }
}
