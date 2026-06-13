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
package com.peco2282.bcreborn.api.items;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for items that represent a list of other items (e.g., BuildCraft Lists).
 * Extends {@link INamedItem}.
 */
public interface IList extends INamedItem {

  /**
   * Gets the label for the list.
   *
   * @param stack The item stack.
   * @return The label string.
   * @deprecated Use {@link INamedItem#getName(ItemStack)} if applicable.
   */
  @Deprecated
  String getLabel(ItemStack stack);

  /**
   * Checks if the given item matches the list.
   *
   * @param stackList The list item stack.
   * @param item      The item to check.
   * @return True if it matches.
   */
  boolean matches(ItemStack stackList, ItemStack item);
}
