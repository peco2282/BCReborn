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
package com.peco2282.bcreborn.core.recipes;

import com.peco2282.bcreborn.api.recipes.IProgrammingRecipe;
import com.peco2282.bcreborn.api.recipes.IProgrammingRecipeManager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class ProgrammingRecipeManager implements IProgrammingRecipeManager {
	public static final ProgrammingRecipeManager INSTANCE = new ProgrammingRecipeManager();
	private final HashMap<String, IProgrammingRecipe> recipes = new HashMap<String, IProgrammingRecipe>();

	@Override
	public void addRecipe(IProgrammingRecipe recipe) {
		if (recipe == null || recipe.getId() == null) {
			return;
		}

		if (!recipes.containsKey(recipe.getId())) {
			recipes.put(recipe.getId(), recipe);
		} else {
			// BCLog.logger.warn("Programming Table Recipe '" + recipe.getId() + "' seems to be duplicated! This is a bug!");
		}
	}

	@Override
	public void removeRecipe(String id) {
		recipes.remove(id);
	}

	@Override
	public void removeRecipe(IProgrammingRecipe recipe) {
		if (recipe == null || recipe.getId() == null) {
			return;
		}

		recipes.remove(recipe.getId());
	}

	@Override
	public Collection<IProgrammingRecipe> getRecipes() {
		return Collections.unmodifiableCollection(recipes.values());
	}

	@Override
	public IProgrammingRecipe getRecipe(String id) {
		return recipes.get(id);
	}
}
