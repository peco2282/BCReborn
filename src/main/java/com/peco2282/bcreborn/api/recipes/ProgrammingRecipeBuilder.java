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

/**
 * Builder class for constructing {@link ProgrammingRecipe} instances.
 * <p>
 * This builder provides a fluent interface for creating programming recipes, which are used
 * to define recipes that require input items, optional components, energy costs, and produce
 * a specific result item. The builder follows the builder pattern, allowing method chaining
 * for convenient recipe definition.
 * <p>
 * Example usage:
 * <pre>{@code
 * ProgrammingRecipe recipe = ProgrammingRecipeBuilder.create(new ResourceLocation("modid", "recipe_name"))
 *     .setInput(Ingredient.of(Items.IRON_INGOT))
 *     .setOption(Ingredient.of(Items.REDSTONE))
 *     .setResult(new ItemStack(Items.DIAMOND))
 *     .setEnergy(1000)
 *     .build();
 * }</pre>
 */
public class ProgrammingRecipeBuilder extends BCRecipeBuilder<ProgrammingRecipe> {
  private Ingredient input;
  private Ingredient option;
  private ItemStack result;
  private int energy;

  /**
   * Constructs a new ProgrammingRecipeBuilder with the specified recipe identifier.
   *
   * @param id The unique resource location identifier for this recipe.
   */
  public ProgrammingRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  /**
   * Sets the input ingredient required for this programming recipe.
   *
   * @param input The ingredient that serves as the primary input for the recipe.
   * @return This builder instance for method chaining.
   */
  public ProgrammingRecipeBuilder setInput(Ingredient input) {
    this.input = input;
    return this;
  }

  /**
   * Sets the optional ingredient for this programming recipe.
   *
   * @param option The ingredient that serves as an optional component for the recipe.
   * @return This builder instance for method chaining.
   */
  public ProgrammingRecipeBuilder setOption(Ingredient option) {
    this.option = option;
    return this;
  }

  /**
   * Sets the result item stack produced by this programming recipe.
   *
   * @param result The item stack that will be produced when the recipe is crafted.
   * @return This builder instance for method chaining.
   */
  public ProgrammingRecipeBuilder setResult(ItemStack result) {
    this.result = result;
    return this;
  }

  /**
   * Sets the energy cost required to execute this programming recipe.
   *
   * @param energy The amount of energy required for the recipe, in the mod's energy units.
   * @return This builder instance for method chaining.
   */
  public ProgrammingRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  /**
   * Creates a new ProgrammingRecipeBuilder instance with the specified recipe identifier.
   * <p>
   * This is a convenience factory method that provides an alternative to using the constructor directly.
   *
   * @param id The unique resource location identifier for this recipe.
   * @return A new ProgrammingRecipeBuilder instance.
   */
  public static ProgrammingRecipeBuilder create(ResourceLocation id) {
    return new ProgrammingRecipeBuilder(id);
  }

  /**
   * Builds and returns the final {@link ProgrammingRecipe} instance using the configured parameters.
   * <p>
   * This method creates a new ProgrammingRecipe with all the properties that have been set on this builder.
   *
   * @return A new ProgrammingRecipe instance with the configured input, option, result, and energy values.
   */
  @Override
  public ProgrammingRecipe build() {
    return new ProgrammingRecipe(id, input, option, result, energy);
  }
}
