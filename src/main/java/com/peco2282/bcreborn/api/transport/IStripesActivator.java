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

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for the stripes pipe activator, providing methods to handle items after an action.
 */
public interface IStripesActivator {

  /**
   * Sends the item into the pipe system.
   *
   * @param itemStack The stack to send.
   * @param direction The direction to send it in.
   */
  void sendItem(ItemStack itemStack, Direction direction);

  /**
   * Drops the item into the world.
   *
   * @param itemStack The stack to drop.
   * @param direction The direction to drop it from.
   */
  void dropItem(ItemStack itemStack, Direction direction);
}
