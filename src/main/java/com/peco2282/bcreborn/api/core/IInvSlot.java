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


import net.minecraft.world.item.ItemStack;

public interface IInvSlot {
  /**
   * Returns the slot number of the underlying Inventory.
   *
   * @return the slot number
   */
  int getIndex();

  boolean canPutStackInSlot(ItemStack stack);

  boolean canTakeStackFromSlot(ItemStack stack);

  boolean isItemValidForSlot(ItemStack stack);

  ItemStack decreaseStackInSlot(int amount);

  ItemStack getStackInSlot();

  void setStackInSlot(ItemStack stack);
}
