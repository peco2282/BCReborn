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

/**
 * Abstract builder class for constructing {@link BCRecipe} instances.
 * <p>
 * Implementations should provide specific configuration methods and
 * build logic for their respective recipe types.
 *
 * @param <T> The type of recipe this builder creates, must extend {@link BCRecipe}.
 */
public abstract class BCRecipeBuilder<T extends BCRecipe> {
  /**
   * The unique identifier for the recipe being built.
   */
  protected final ResourceLocation id;

  /**
   * Constructs a new recipe builder with the specified identifier.
   *
   * @param id The unique {@link ResourceLocation} identifier for the recipe.
   */
  public BCRecipeBuilder(ResourceLocation id) {
    this.id = id;
  }

  /**
   * Builds and returns the configured recipe instance.
   *
   * @return The constructed recipe of type {@code T}.
   */
  public abstract T build();
}
