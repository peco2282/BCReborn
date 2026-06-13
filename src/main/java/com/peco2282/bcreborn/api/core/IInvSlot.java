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

/**
 * Interface representing a slot in an inventory.
 */
public interface IInvSlot {

  /**
   * Returns the slot number of the underlying Inventory.
   *
   * @return the slot index.
   */
  int getIndex();

  /**
   * Checks if the given stack can be placed into this slot.
   *
   * @param stack The stack to check.
   * @return True if the stack can be placed.
   */
  boolean canPutStackInSlot(ItemStack stack);

  /**
   * Checks if the given stack can be removed from this slot.
   *
   * @param stack The stack to check.
   * @return True if the stack can be taken.
   */
  boolean canTakeStackFromSlot(ItemStack stack);

  /**
   * Checks if the given stack is valid for this slot.
   *
   * @param stack The stack to check.
   * @return True if the item is valid.
   */
  boolean isItemValidForSlot(ItemStack stack);

  /**
   * Decreases the stack size in this slot by the specified amount.
   *
   * @param amount The amount to decrease by.
   * @return The resulting {@link ItemStack}.
   */
  ItemStack decreaseStackInSlot(int amount);

  /**
   * Gets the {@link ItemStack} currently in this slot.
   *
   * @return The stack in the slot.
   */
  ItemStack getStackInSlot();

  /**
   * Sets the {@link ItemStack} in this slot.
   *
   * @param stack The stack to set.
   */
  void setStackInSlot(ItemStack stack);
}
