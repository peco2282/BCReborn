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

import com.peco2282.bcreborn.api.recipes.IIntegrationRecipe;
import com.peco2282.bcreborn.api.recipes.IIntegrationRecipeManager;

import java.util.LinkedList;
import java.util.List;

public class IntegrationRecipeManager implements IIntegrationRecipeManager {
	public static final IntegrationRecipeManager INSTANCE = new IntegrationRecipeManager();
	private List<IIntegrationRecipe> integrationRecipes = new LinkedList<IIntegrationRecipe>();

	@Override
	public void addRecipe(IIntegrationRecipe recipe) {
		integrationRecipes.add(recipe);
	}

	@Override
	public List<? extends IIntegrationRecipe> getRecipes() {
		return integrationRecipes;
	}
}
