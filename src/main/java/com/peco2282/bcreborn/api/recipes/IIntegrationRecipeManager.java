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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Interface for managing integration recipes.
 * Provides methods to retrieve, query, and manage integration recipes by their resource location.
 */
public interface IIntegrationRecipeManager {
  /**
   * Retrieves an integration recipe by its resource location.
   *
   * @param id The resource location of the recipe.
   * @return The integration recipe, or null if not found.
   */
  @Nullable IntegrationRecipe getRecipe(ResourceLocation id);

  /**
   * Gets all registered integration recipes.
   *
   * @return A collection of all integration recipes.
   */
  Collection<IntegrationRecipe> getRecipes();

  /**
   * Checks if a recipe with the specified resource location exists.
   *
   * @param id The resource location to check.
   * @return True if the recipe exists, false otherwise.
   */
  boolean contains(ResourceLocation id);
}
