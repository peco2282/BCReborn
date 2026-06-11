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

import com.peco2282.bcreborn.api.recipes.IFlexibleRecipe;
import com.peco2282.bcreborn.api.recipes.IRefineryRecipeManager;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public final class RefineryRecipeManager implements IRefineryRecipeManager {

	public static final RefineryRecipeManager INSTANCE = new RefineryRecipeManager();
	private HashMap<String, IFlexibleRecipe<FluidStack>> recipes = new HashMap<String, IFlexibleRecipe<FluidStack>>();
	private ArrayList<FluidStack> validFluids1 = new ArrayList<FluidStack>();
	private ArrayList<FluidStack> validFluids2 = new ArrayList<FluidStack>();

	private RefineryRecipeManager() {
	}

	@Override
	public void addRecipe(String id, FluidStack ingredient, FluidStack result, int energy, int delay) {
		FlexibleRecipe<FluidStack> recipe = new FlexibleRecipe<FluidStack>(id, result, energy, delay, ingredient);
		recipes.put(id, recipe);
		validFluids1.add(ingredient);
		validFluids2.add(ingredient);
	}

	@Override
	public void addRecipe(String id, FluidStack ingredient1, FluidStack ingredient2, FluidStack result, int energy,
						  int delay) {

		if (ingredient1 == null || ingredient2 == null || result == null) {
			// BCLog.logger.warn("Rejected refinery recipe " + id + " due to a null FluidStack!");
		}

		FlexibleRecipe<FluidStack> recipe = new FlexibleRecipe<FluidStack>(id, result, energy, delay, ingredient1,
				ingredient2);
		recipes.put(id, recipe);
		validFluids1.add(ingredient1);
		validFluids2.add(ingredient2);
	}

	@Override
	public Collection<IFlexibleRecipe<FluidStack>> getRecipes() {
		return Collections.unmodifiableCollection(recipes.values());
	}

	@Override
	public IFlexibleRecipe<FluidStack> getRecipe(String id) {
		return recipes.get(id);
	}

	@Override
	public void removeRecipe(IFlexibleRecipe<FluidStack> recipe) {
		removeRecipe(recipe.getId());
	}

	@Override
	public void removeRecipe(String id) {
		recipes.remove(id);
	}

	public ArrayList<FluidStack> getValidFluidStacks1() {
		return validFluids1;
	}

	public ArrayList<FluidStack> getValidFluidStacks2() {
		return validFluids2;
	}
}
