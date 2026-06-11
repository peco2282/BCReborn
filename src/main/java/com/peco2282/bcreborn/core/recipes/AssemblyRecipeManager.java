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

import com.peco2282.bcreborn.api.recipes.IAssemblyRecipeManager;
import com.peco2282.bcreborn.api.recipes.IFlexibleRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssemblyRecipeManager implements IAssemblyRecipeManager {

	public static final AssemblyRecipeManager INSTANCE = new AssemblyRecipeManager();
	private Map<String, IFlexibleRecipe<ItemStack>> assemblyRecipes = new HashMap<String, IFlexibleRecipe<ItemStack>>();

	@Override
	public void addRecipe(String id, int energyCost, ItemStack output, Object... input) {
		addRecipe(id, new FlexibleRecipe<ItemStack>(id, output, energyCost, 0, input));
	}

	@Override
	public void addRecipe(IFlexibleRecipe<ItemStack> recipe) {
		addRecipe(recipe.getId(), recipe);
	}

	private void addRecipe(String id, IFlexibleRecipe<ItemStack> recipe) {
		if (recipe == null) {
			throw new RuntimeException("Recipe \"" + id + "\" is null!");
		}

		if (assemblyRecipes.containsKey(id)) {
			throw new RuntimeException("Recipe \"" + id + "\" already registered");
		}

		assemblyRecipes.put(recipe.getId(), recipe);
	}

	@Override
	public Collection<IFlexibleRecipe<ItemStack>> getRecipes() {
		return assemblyRecipes.values();
	}

	public IFlexibleRecipe<ItemStack> getRecipe(String id) {
		return assemblyRecipes.get(id);
	}

	@Override
	public void removeRecipe(IFlexibleRecipe<ItemStack> recipe) {
		removeRecipe(recipe.getId());
	}

	@Override
	public void removeRecipe(String id) {
		assemblyRecipes.remove(id);
	}
}
