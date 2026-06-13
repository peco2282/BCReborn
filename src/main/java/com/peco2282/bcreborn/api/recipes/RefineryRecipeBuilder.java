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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Builder class for creating {@link RefineryRecipe} instances.
 * <p>
 * This builder provides a fluent interface for constructing refinery recipes with
 * a primary ingredient, optional secondary ingredient, result fluid, energy requirement,
 * and processing delay.
 */
public class RefineryRecipeBuilder extends BCRecipeBuilder<RefineryRecipe> {
  private Ingredient primary;
  private @Nullable Ingredient secondary;
  private FluidStack result;
  private int energy;
  private int delay;

  /**
   * Creates a new refinery recipe builder with the specified recipe ID.
   *
   * @param id The resource location identifier for the recipe.
   */
  private RefineryRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  /**
   * Sets the primary ingredient for the refinery recipe.
   *
   * @param primary The primary ingredient required for this recipe.
   * @return This builder instance for method chaining.
   */
  public RefineryRecipeBuilder setPrimary(Ingredient primary) {
    this.primary = primary;
    return this;
  }

  /**
   * Sets the optional secondary ingredient for the refinery recipe.
   *
   * @param secondary The secondary ingredient, or null if not required.
   * @return This builder instance for method chaining.
   */
  public RefineryRecipeBuilder setSecondary(@Nullable Ingredient secondary) {
    this.secondary = secondary;
    return this;
  }

  /**
   * Sets the result fluid stack produced by this refinery recipe.
   *
   * @param result The fluid stack that will be produced.
   * @return This builder instance for method chaining.
   */
  public RefineryRecipeBuilder setResult(FluidStack result) {
    this.result = result;
    return this;
  }

  /**
   * Sets the energy requirement for this refinery recipe.
   *
   * @param energy The amount of energy required to process this recipe.
   * @return This builder instance for method chaining.
   */
  public RefineryRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  /**
   * Sets the processing delay for this refinery recipe.
   *
   * @param delay The number of ticks required to process this recipe.
   * @return This builder instance for method chaining.
   */
  public RefineryRecipeBuilder setDelay(int delay) {
    this.delay = delay;
    return this;
  }

  /**
   * Builds and returns the configured {@link RefineryRecipe}.
   *
   * @return A new refinery recipe instance with the configured parameters.
   */
  @Override
  public RefineryRecipe build() {
    return new RefineryRecipe(id, primary, Optional.ofNullable(secondary), result, energy, delay);
  }
}
