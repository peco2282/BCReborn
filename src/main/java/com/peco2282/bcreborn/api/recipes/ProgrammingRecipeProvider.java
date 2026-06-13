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
 * Abstract base class for providing programming recipes in BuildCraft Reborn.
 * <p>
 * This class extends {@link BCRecipeProvider} to generate programming recipes, which are used
 * to program robotic entities and redstone boards within the mod. It handles the serialization
 * of programming recipes into data pack JSON files.
 * <p>
 * Implementations should override the recipe generation methods to define specific programming
 * recipes for their mod.
 *
 * @see BCRecipeProvider
 * @see ProgrammingRecipe
 * @see ProgrammingRecipeBuilder
 */
public abstract class ProgrammingRecipeProvider extends BCRecipeProvider<ProgrammingRecipe, ProgrammingRecipeBuilder> {
  /**
   * The directory path within the data pack where programming recipe JSON files are stored.
   * <p>
   * This constant defines the subdirectory structure for organizing programming recipes
   * in the mod's data pack output.
   */
  public static final String DIRECTORY = kind("programming");

  /**
   * Constructs a new programming recipe provider.
   * <p>
   * Initializes the provider with the specified pack output and mod ID, configuring
   * the path provider to target the data pack's programming recipe directory.
   *
   * @param output The pack output used to create file paths for generated recipes.
   * @param modId  The mod identifier for which these programming recipes are being generated.
   */
  public ProgrammingRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), ProgrammingRecipe.CODEC, modId);
  }

  /**
   * Returns a human-readable name for this recipe provider.
   * <p>
   * This name is used for logging and identification purposes during data generation.
   *
   * @return A string describing this provider as "Programming recipes for [modId]".
   */
  @Override
  public String getName() {
    return "Programming recipes for " + modId;
  }
}
