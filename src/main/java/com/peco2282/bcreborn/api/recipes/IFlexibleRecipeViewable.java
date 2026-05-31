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

/**
 * This class is intended for mods such as Not Enough Items
 * in order for them to be able to look inside a recipe.
 * 
 * It is intentionally left as a separate interface, so that
 * it remains possible to register a "dynamic" flexible
 * recipe which does not have static inputs and outputs.
 * 
 * @author asie
 */
public interface IFlexibleRecipeViewable {
	Object getOutput();
	
	/**
	 * With BuildCraft's implementation (as of 6.1.3), this might
	 * contain either an ItemStack, a List&lt;ItemStack&gt; or a FluidStack.
	 */
	Collection<Object> getInputs();
	
	long getCraftingTime();
	
	int getEnergyCost();
}
