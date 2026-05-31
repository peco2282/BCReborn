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

import java.util.Collection;

public interface IRecipeManager<T> {

	/**
	 * Add a recipe.
	 *
	 * @param input
	 *            Object... containing either an ItemStack, or a paired string
	 *            and integer(ex: "dyeBlue", 1)
	 * @param energyCost
	 *            RF cost to produce
	 * @param output
	 *            resulting ItemStack
	 */
	void addRecipe(String id, int energyCost, T output, Object... input);

	void addRecipe(String id, int energyCost, int craftingDelay, T output, Object... input);

	void addRecipe(IFlexibleRecipe<T> recipe);
	
	void removeRecipe(String id);

	void removeRecipe(IFlexibleRecipe<T> recipe);

	Collection<IFlexibleRecipe<T>> getRecipes();

	IFlexibleRecipe<T> getRecipe(String id);
}
