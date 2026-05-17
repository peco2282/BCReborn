/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
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
