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
 * Manager interface for programming recipes in BuildCraft.
 * <p>
 * This interface provides methods to register, retrieve, and query programming recipes
 * used in BuildCraft's programming systems. Implementations should handle both static
 * and dynamic recipe registration.
 * </p>
 */
public interface IProgrammingRecipeManager {
  /**
   * Retrieves a programming recipe by its unique identifier.
   *
   * @param id the resource location identifier of the recipe
   * @return the programming recipe with the given id, or null if no such recipe exists
   */
  @Nullable IProgrammingRecipe getRecipe(ResourceLocation id);

  /**
   * Gets all registered programming recipes.
   *
   * @return an unmodifiable collection of all registered programming recipes
   */
  Collection<IProgrammingRecipe> getRecipes();

  /**
   * Checks if a recipe with the given identifier exists.
   *
   * @param id the resource location identifier to check
   * @return true if a recipe with the given id is registered, false otherwise
   */
  boolean contains(ResourceLocation id);

  /**
   * Dynamically registers a new programming recipe.
   * <p>
   * This method allows runtime registration of recipes beyond those loaded from data packs.
   * If a recipe with the same identifier already exists, the behavior is implementation-dependent.
   * </p>
   *
   * @param recipe the programming recipe to register
   */
  void register(IProgrammingRecipe recipe);
}
