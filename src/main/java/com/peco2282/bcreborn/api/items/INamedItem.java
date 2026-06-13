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

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for items that can have a custom name (e.g., via a BuildCraft programmer).
 */
public interface INamedItem {

  /**
   * Gets the custom name of the item.
   *
   * @param stack The item stack.
   * @return The custom name as a {@link Component}.
   */
  Component getName(ItemStack stack);

  /**
   * Sets the custom name of the item.
   *
   * @param stack The item stack.
   * @param name  The new custom name.
   * @return True if the name was successfully set.
   */
  boolean setName(ItemStack stack, Component name);
}
