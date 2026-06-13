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

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating {@link IntegrationRecipe} instances.
 * Provides a fluent API for configuring integration recipe parameters.
 */
public final class IntegrationRecipeBuilder extends BCRecipeBuilder<IntegrationRecipe> {
  private Ingredient input;
  private final List<Ingredient> expansions = new ArrayList<>();
  private ItemStack result;
  private int energy;
  private int maxExpansionCount;

  /**
   * Private constructor. Use {@link #create(ResourceLocation)} to instantiate.
   *
   * @param id The recipe identifier.
   */
  private IntegrationRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  /**
   * Sets the input ingredient for the recipe.
   *
   * @param input The input ingredient.
   * @return This builder for method chaining.
   */
  public IntegrationRecipeBuilder setInput(Ingredient input) {
    this.input = input;
    return this;
  }

  /**
   * Adds an expansion ingredient to the recipe.
   *
   * @param expansion The expansion ingredient to add.
   * @return This builder for method chaining.
   */
  public IntegrationRecipeBuilder addExpansion(Ingredient expansion) {
    this.expansions.add(expansion);
    return this;
  }

  /**
   * Sets the result item stack for the recipe.
   *
   * @param result The resulting item stack.
   * @return This builder for method chaining.
   */
  public IntegrationRecipeBuilder setResult(ItemStack result) {
    this.result = result;
    return this;
  }

  /**
   * Sets the energy required for the recipe.
   *
   * @param energy The energy amount.
   * @return This builder for method chaining.
   */
  public IntegrationRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  /**
   * Sets the maximum number of expansions allowed for the recipe.
   *
   * @param maxExpansionCount The maximum expansion count.
   * @return This builder for method chaining.
   */
  public IntegrationRecipeBuilder setMaxExpansionCount(int maxExpansionCount) {
    this.maxExpansionCount = maxExpansionCount;
    return this;
  }

  /**
   * Creates a new builder instance with the specified recipe identifier.
   *
   * @param id The recipe identifier.
   * @return A new IntegrationRecipeBuilder instance.
   */
  public static IntegrationRecipeBuilder create(ResourceLocation id) {
    return new IntegrationRecipeBuilder(id);
  }

  /**
   * Builds and returns the configured {@link IntegrationRecipe}.
   *
   * @return The constructed IntegrationRecipe instance.
   */
  @Override
  public IntegrationRecipe build() {
    return new IntegrationRecipe(id, input, expansions, result, energy, maxExpansionCount);
  }
}
