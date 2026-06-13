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

/**
 * Builder class for creating {@link AssemblyRecipe} instances.
 * <p>
 * This builder follows the fluent API pattern, allowing method chaining
 * to configure all aspects of an assembly recipe including ingredients,
 * result item, energy cost, and crafting time.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * AssemblyRecipeBuilder.create(new ResourceLocation("modid", "recipe_name"))
 *     .addIngredient(Items.IRON_INGOT)
 *     .addIngredient(Items.REDSTONE)
 *     .setResult(new ItemStack(customItem))
 *     .setEnergy(1000)
 *     .setCraftingTime(200)
 *     .build();
 * </pre>
 * </p>
 *
 * @see AssemblyRecipe
 * @see BCRecipeBuilder
 */
public class AssemblyRecipeBuilder extends BCRecipeBuilder<AssemblyRecipe> {
  private final List<Ingredient> ingredients = new ArrayList<>();
  private ItemStack result;
  private int energy;
  private int craftingTime;

  /**
   * Private constructor to enforce use of the factory method.
   *
   * @param id The resource location identifier for the recipe
   */
  private AssemblyRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  /**
   * Adds an ingredient to the recipe from an ItemStack.
   *
   * @param stack The ItemStack to add as an ingredient
   * @return This builder instance for method chaining
   */
  public AssemblyRecipeBuilder addIngredient(ItemStack stack) {
    this.addIngredient(Ingredient.of(stack));
    return this;
  }

  /**
   * Adds an ingredient to the recipe from one or more ItemLike objects.
   *
   * @param items One or more ItemLike objects (Items, Blocks, etc.) to add as an ingredient
   * @return This builder instance for method chaining
   */
  public AssemblyRecipeBuilder addIngredient(ItemLike... items) {
    this.addIngredient(Ingredient.of(items));
    return this;
  }

  /**
   * Adds an ingredient to the recipe.
   *
   * @param ingredient The Ingredient to add to the recipe
   * @return This builder instance for method chaining
   */
  public AssemblyRecipeBuilder addIngredient(Ingredient ingredient) {
    ingredients.add(ingredient);
    return this;
  }

  /**
   * Sets the result ItemStack produced by this recipe.
   *
   * @param result The ItemStack that will be crafted
   * @return This builder instance for method chaining
   */
  public AssemblyRecipeBuilder setResult(ItemStack result) {
    this.result = result;
    return this;
  }

  /**
   * Sets the energy cost required to craft this recipe.
   *
   * @param energy The amount of energy required (in FE/RF units)
   * @return This builder instance for method chaining
   */
  public AssemblyRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  /**
   * Sets the time required to craft this recipe.
   *
   * @param craftingTime The crafting duration in ticks (20 ticks = 1 second)
   * @return This builder instance for method chaining
   */
  public AssemblyRecipeBuilder setCraftingTime(int craftingTime) {
    this.craftingTime = craftingTime;
    return this;
  }

  /**
   * Builds and returns the configured AssemblyRecipe instance.
   *
   * @return A new AssemblyRecipe with the configured parameters
   */
  @Override
  public AssemblyRecipe build() {
    return new AssemblyRecipe(id, ingredients, result, energy, craftingTime);
  }

  /**
   * Creates a new AssemblyRecipeBuilder instance.
   * <p>
   * This is the recommended way to instantiate the builder.
   * </p>
   *
   * @param id The resource location identifier for the recipe
   * @return A new AssemblyRecipeBuilder instance
   */
  public static AssemblyRecipeBuilder create(ResourceLocation id) {
    return new AssemblyRecipeBuilder(id);
  }
}
