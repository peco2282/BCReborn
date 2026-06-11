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
import net.minecraft.world.item.ItemStack;

import java.lang.ref.SoftReference;
import java.util.List;

public abstract class IntegrationRecipeBC implements IIntegrationRecipe {
	private final int energyCost, maxExpansionCount;
	private SoftReference<List<ItemStack>> exampleInputs;
	private SoftReference<List<ItemStack>> exampleOutputs;
	private SoftReference<List<List<ItemStack>>> exampleExpansions;

	public IntegrationRecipeBC(int energyCost) {
		this(energyCost, -1);
	}

	public IntegrationRecipeBC(int energyCost, int maxExpansionCount) {
		this.energyCost = energyCost;
		this.maxExpansionCount = maxExpansionCount;
	}

	public abstract List<ItemStack> generateExampleInput();

	public abstract List<ItemStack> generateExampleOutput();

	public abstract List<List<ItemStack>> generateExampleExpansions();

	@Override
	public int getEnergyCost() {
		return energyCost;
	}

	@Override
	public List<ItemStack> getExampleInput() {
		if (exampleInputs != null && exampleInputs.get() != null) {
			return exampleInputs.get();
		}
		exampleInputs = new SoftReference<List<ItemStack>>(generateExampleInput());
		return exampleInputs.get();
	}

	@Override
	public List<List<ItemStack>> getExampleExpansions() {
		if (exampleExpansions != null && exampleExpansions.get() != null) {
			return exampleExpansions.get();
		}
		exampleExpansions = new SoftReference<List<List<ItemStack>>>(generateExampleExpansions());
		return exampleExpansions.get();
	}

	@Override
	public List<ItemStack> getExampleOutput() {
		if (exampleOutputs != null && exampleOutputs.get() != null) {
			return exampleOutputs.get();
		}
		exampleOutputs = new SoftReference<List<ItemStack>>(generateExampleOutput());
		return exampleOutputs.get();
	}

	@Override
	public int getMaximumExpansionCount(ItemStack input) {
		return maxExpansionCount;
	}
}
