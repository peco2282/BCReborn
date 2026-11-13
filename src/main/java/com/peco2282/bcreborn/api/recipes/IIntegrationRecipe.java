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

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A dynamic Integration Table recipe (original {@code buildcraft.api.recipes.IIntegrationRecipe}).
 * Unlike the data-driven {@link IntegrationRecipe}, the output depends on the input and the inserted expansions.
 */
public interface IIntegrationRecipe {
  /**
   * @return The energy cost of the recipe.
   */
  int getEnergyCost();

  /**
   * @return A list of example inputs, used for recipe previews.
   */
  List<ItemStack> getExampleInput();

  /**
   * @return A list of every slot's list of example expansions. If there are fewer lists than the maximum
   * expansion count, the lists are repeated.
   */
  List<List<ItemStack>> getExampleExpansions();

  /**
   * @return A list of example outputs, used for recipe previews.
   */
  List<ItemStack> getExampleOutput();

  /**
   * @param input The input.
   * @return Whether the input is valid.
   */
  boolean isValidInput(ItemStack input);

  /**
   * @param input     The input currently in the table.
   * @param expansion The expansion.
   * @return Whether the expansion can be fitted to the given input.
   */
  boolean isValidExpansion(ItemStack input, ItemStack expansion);

  /**
   * Craft the recipe. The recipe decreases the expansions itself; the input is decreased by the caller.
   *
   * @param input      The input.
   * @param expansions All inserted expansions.
   * @param preview    If true, do not decrease the expansions.
   * @return The output stack, or null if the expansions do not give a result.
   */
  @Nullable
  ItemStack craft(ItemStack input, List<ItemStack> expansions, boolean preview);

  /**
   * @return The maximum count of expansions this recipe can have, or -1 for no limit.
   */
  int getMaximumExpansionCount(ItemStack input);
}
