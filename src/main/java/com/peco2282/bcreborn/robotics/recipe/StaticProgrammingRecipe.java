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
package com.peco2282.bcreborn.robotics.recipe;

import com.peco2282.bcreborn.api.recipes.IProgrammingRecipe;
import com.peco2282.bcreborn.api.recipes.ProgrammingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class StaticProgrammingRecipe implements IProgrammingRecipe {
  private final ProgrammingRecipe recipe;

  public StaticProgrammingRecipe(ProgrammingRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public ResourceLocation getId() {
    return recipe.id();
  }

  @Override
  public List<ItemStack> getOptions(int width, int height) {
    return List.of(recipe.option().getItems());
  }

  @Override
  public int getEnergyCost(ItemStack option) {
    return recipe.energy();
  }

  @Override
  public boolean canCraft(ItemStack input) {
    return recipe.input().test(input);
  }

  @Override
  public ItemStack craft(ItemStack input, ItemStack option) {
    if (!canCraft(input)) return ItemStack.EMPTY;
    if (!recipe.option().test(option)) return ItemStack.EMPTY;

    return recipe.result().copy();
  }
}
