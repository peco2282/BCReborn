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

public abstract class ProgrammingRecipeProvider extends BCRecipeProvider<ProgrammingRecipe, ProgrammingRecipeBuilder> {
  public static final String DIRECTORY = kind("programming");

  public ProgrammingRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), ProgrammingRecipe.CODEC, modId);
  }

  @Override
  public String getName() {
    return "Programming recipes for " + modId;
  }
}
