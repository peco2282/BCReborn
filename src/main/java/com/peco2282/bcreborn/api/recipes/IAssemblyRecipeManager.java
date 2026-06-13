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
 * Manager interface for assembly recipes.
 * Provides methods to retrieve and query assembly recipes by their resource location.
 */
public interface IAssemblyRecipeManager {
  /**
   * Gets an assembly recipe by its resource location ID.
   *
   * @param id The resource location identifier of the recipe.
   * @return The assembly recipe, or null if not found.
   */
  @Nullable AssemblyRecipe getRecipe(ResourceLocation id);

  /**
   * Gets all registered assembly recipes.
   *
   * @return A collection of all assembly recipes.
   */
  Collection<AssemblyRecipe> getRecipes();

  /**
   * Checks if a recipe with the given ID exists.
   *
   * @param id The resource location identifier of the recipe.
   * @return True if the recipe exists, false otherwise.
   */
  boolean contains(ResourceLocation id);
}
