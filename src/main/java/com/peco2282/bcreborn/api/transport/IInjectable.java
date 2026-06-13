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
 * Interface for objects that can have items injected into them.
 */
public interface IInjectable {

  /**
   * Checks if items can be injected from the specified side.
   *
   * @param from The direction items are coming from.
   * @return True if injection is allowed.
   */
  boolean canInjectItems(Direction from);

  /**
   * Offers an {@link ItemStack} for addition to the object.
   * Will be rejected if the object doesn't accept items from that side.
   *
   * @param stack The stack to inject. Do not modify the original stack!
   * @param doAdd If true, actually perform the injection; if false, just simulate.
   * @param from  The direction the item is coming from.
   * @param color The color of the item (if any), or null.
   * @return The number of items successfully injected.
   */
  int injectItem(ItemStack stack, boolean doAdd, Direction from, Integer color);
}
