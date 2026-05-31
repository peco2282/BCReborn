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
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class CraftingResult<T> {
    public T crafted = null;
    public ArrayList<ItemStack> usedItems = new ArrayList<>();
    public ArrayList<FluidStack> usedFluids = new ArrayList<>();
    public int energyCost = 0;
    public long craftingTime = 0;
    public IFlexibleRecipe<T> recipe;
}
