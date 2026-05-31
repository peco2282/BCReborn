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

import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public interface IRefineryRecipeManager {

  void addRecipe(String id, FluidStack ingredient, FluidStack result, int energy, int delay);

  void addRecipe(String id, FluidStack ingredient1, FluidStack ingredient2, FluidStack result, int energy, int delay);

  void removeRecipe(String id);

  void removeRecipe(IFlexibleRecipe<FluidStack> recipe);

  Collection<IFlexibleRecipe<FluidStack>> getRecipes();

  IFlexibleRecipe<FluidStack> getRecipe(String currentRecipeId);

}
