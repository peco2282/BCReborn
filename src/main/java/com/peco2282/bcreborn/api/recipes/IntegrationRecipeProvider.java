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
 * Abstract provider for generating integration recipes.
 * <p>
 * This class extends {@link BCRecipeProvider} and is designed to facilitate
 * the creation and management of integration recipes for mods that integrate
 * with BuildCraft Reborn. Integration recipes are stored in the data pack
 * under the "integration" directory.
 * </p>
 *
 * @see BCRecipeProvider
 * @see IntegrationRecipe
 * @see IntegrationRecipeBuilder
 */
public abstract class IntegrationRecipeProvider extends BCRecipeProvider<IntegrationRecipe, IntegrationRecipeBuilder> {
  /**
   * The directory path where integration recipes are stored within the data pack.
   */
  public static final String DIRECTORY = kind("integration");

  /**
   * Constructs a new IntegrationRecipeProvider.
   *
   * @param output The pack output for generating recipe files.
   * @param modId  The mod ID for which recipes are being generated.
   */
  public IntegrationRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), IntegrationRecipe.CODEC, modId);
  }

  /**
   * Returns the descriptive name of this recipe provider.
   *
   * @return A string describing the integration recipes for the associated mod ID.
   */
  @Override
  public String getName() {
    return "Integration recipes for " + modId;
  }
}
