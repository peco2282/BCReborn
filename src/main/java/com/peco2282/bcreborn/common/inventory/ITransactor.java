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
package com.peco2282.bcreborn.common.inventory;


import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface ITransactor {

  /**
   * Adds an Item to the inventory.
   *
   * @param stack The item stack to add.
   * @param orientation The orientation of the inventory.
   * @param doAdd Whether to actually add the item.
   * @return The ItemStack, with stackSize equal to amount moved.
   */
  ItemStack add(ItemStack stack, Direction orientation, boolean doAdd);

  /**
   * Removes and returns a single item from the inventory matching the filter.
   *
   * @param filter The filter to match against.
   * @param orientation The orientation of the inventory.
   * @param doRemove Whether to actually remove the item.
   */
  ItemStack remove(IStackFilter filter, Direction orientation, boolean doRemove);
}
