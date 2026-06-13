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

import net.minecraft.data.PackOutput;

/**
 * Abstract base class for providing refinery recipes via data generation.
 * <p>
 * This class extends {@link BCRecipeProvider} to provide a foundation for creating
 * refinery recipes in a structured manner. Subclasses should implement the recipe
 * generation logic specific to their mod.
 * <p>
 * Refinery recipes are stored in the data pack under the directory specified by {@link #DIRECTORY}.
 */
public abstract class RefineryRecipeProvider extends BCRecipeProvider<RefineryRecipe, RefineryRecipeBuilder> {
  /**
   * The directory path where refinery recipes are stored within the data pack.
   */
  public static final String DIRECTORY = kind("refinery");

  /**
   * Creates a new refinery recipe provider for the specified mod.
   *
   * @param output The pack output to write recipes to.
   * @param modId  The mod ID that this provider generates recipes for.
   */
  public RefineryRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), RefineryRecipe.CODEC, modId);
  }

  /**
   * Gets the descriptive name of this recipe provider.
   *
   * @return A string identifying this provider as a refinery recipe generator for the current mod.
   */
  @Override
  public String getName() {
    return "Refinery recipes for " + modId;
  }
}
