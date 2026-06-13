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
package com.peco2282.bcreborn.api.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Represents a programming recipe used to apply programming options to items.
 * Programming recipes define how items can be programmed with specific options,
 * the energy cost associated with each option, and the resulting programmed item.
 */
public interface IProgrammingRecipe {
  /**
   * Gets the unique identifier for this programming recipe.
   *
   * @return The recipe's resource location identifier.
   */
  ResourceLocation getId();

  /**
   * Gets the list of available programming options for the given dimensions.
   *
   * @param width  The width of the programming area.
   * @param height The height of the programming area.
   * @return A list of item stacks representing available programming options.
   */
  List<ItemStack> getOptions(int width, int height);

  /**
   * Calculates the energy cost required to apply the specified programming option.
   *
   * @param option The programming option item stack.
   * @return The energy cost in energy units.
   */
  int getEnergyCost(ItemStack option);

  /**
   * Checks if the given input item can be programmed using this recipe.
   *
   * @param input The input item stack to check.
   * @return True if the item can be programmed, false otherwise.
   */
  boolean canCraft(ItemStack input);

  /**
   * Applies the programming option to the input item and returns the programmed result.
   *
   * @param input  The input item stack to be programmed.
   * @param option The programming option to apply.
   * @return The resulting programmed item stack.
   */
  ItemStack craft(ItemStack input, ItemStack option);
}
