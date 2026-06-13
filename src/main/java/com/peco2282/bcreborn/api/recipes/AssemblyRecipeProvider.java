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
 * Abstract base class for providing assembly recipes in BuildCraft Reborn.
 * <p>
 * This provider is responsible for generating assembly recipe data files
 * during the data generation phase. Extend this class to define custom
 * assembly recipes for your mod.
 * </p>
 */
public abstract class AssemblyRecipeProvider extends BCRecipeProvider<AssemblyRecipe, AssemblyRecipeBuilder> {
  /**
   * The directory path where assembly recipe data files will be generated.
   */
  public static final String DIRECTORY = kind("assembly");

  /**
   * Constructs a new AssemblyRecipeProvider.
   *
   * @param output The pack output for data generation.
   * @param modId  The mod ID for which recipes are being generated.
   */
  public AssemblyRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), AssemblyRecipe.CODEC, modId);
  }

  /**
   * Returns the name of this recipe provider.
   *
   * @return A descriptive name indicating this provider generates assembly recipes.
   */
  @Override
  public String getName() {
    return "Assembly recipes for " + modId;
  }
}
