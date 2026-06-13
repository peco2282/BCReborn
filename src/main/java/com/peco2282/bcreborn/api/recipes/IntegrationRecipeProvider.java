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

public abstract class IntegrationRecipeProvider extends BCRecipeProvider<IntegrationRecipe, IntegrationRecipeBuilder> {
  public static final String DIRECTORY = kind("integration");
  private final String modId;

  public IntegrationRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), IntegrationRecipe.CODEC);
    this.modId = modId;
  }

  @Override
  public String getName() {
    return "Integration recipes for " + modId;
  }
}
