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

import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public interface IAssemblyRecipeManager {
  void addRecipe(String id, int energyCost, ItemStack output, Object... input);

  void addRecipe(IFlexibleRecipe<ItemStack> recipe);

  void removeRecipe(String id);

  void removeRecipe(IFlexibleRecipe<ItemStack> recipe);

  Collection<IFlexibleRecipe<ItemStack>> getRecipes();
}
