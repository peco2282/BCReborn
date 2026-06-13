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
 * Manager interface for BuildCraft refinery recipes.
 * <p>
 * This interface provides access to the registry of refinery recipes, allowing
 * retrieval of individual recipes by their ResourceLocation ID, checking for
 * recipe existence, and obtaining the complete collection of registered recipes.
 * <p>
 * Refinery recipes define how fluids are processed and transformed within the
 * BuildCraft refinery system.
 */
public interface IRefineryRecipeManager {
  /**
   * Retrieves a refinery recipe by its unique ResourceLocation identifier.
   * <p>
   * This method looks up a recipe in the registry using the provided ID.
   *
   * @param id The ResourceLocation identifier of the recipe to retrieve.
   * @return The {@link RefineryRecipe} associated with the given ID, or null if no recipe is found.
   */
  @Nullable RefineryRecipe getRecipe(ResourceLocation id);

  /**
   * Retrieves all registered refinery recipes.
   * <p>
   * This method returns the complete collection of all recipes currently
   * registered in the refinery recipe manager.
   *
   * @return A collection containing all registered {@link RefineryRecipe} instances.
   */
  Collection<RefineryRecipe> getRecipes();

  /**
   * Checks whether a recipe with the specified ResourceLocation ID exists in the registry.
   * <p>
   * This method can be used to verify recipe existence before attempting retrieval.
   *
   * @param id The ResourceLocation identifier to check.
   * @return true if a recipe with the given ID exists, false otherwise.
   */
  boolean contains(ResourceLocation id);
}
