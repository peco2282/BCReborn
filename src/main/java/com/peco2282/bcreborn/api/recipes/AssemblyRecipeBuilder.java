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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class AssemblyRecipeBuilder extends BCRecipeBuilder<AssemblyRecipe> {
  private final List<Ingredient> ingredients = new ArrayList<>();
  private ItemStack result;
  private int energy;
  private int craftingTime;

  private AssemblyRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  public AssemblyRecipeBuilder addIngredient(ItemStack stack) {
    this.addIngredient(Ingredient.of(stack));
    return this;
  }

  public AssemblyRecipeBuilder addIngredient(ItemLike... items) {
    this.addIngredient(Ingredient.of(items));
    return this;
  }

  public AssemblyRecipeBuilder addIngredient(Ingredient ingredient) {
    ingredients.add(ingredient);
    return this;
  }

  public AssemblyRecipeBuilder setResult(ItemStack result) {
    this.result = result;
    return this;
  }

  public AssemblyRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  public AssemblyRecipeBuilder setCraftingTime(int craftingTime) {
    this.craftingTime = craftingTime;
    return this;
  }

  @Override
  public AssemblyRecipe build() {
    return new AssemblyRecipe(id, ingredients, result, energy, craftingTime);
  }

  public static AssemblyRecipeBuilder create(ResourceLocation id) {
    return new AssemblyRecipeBuilder(id);
  }
}
