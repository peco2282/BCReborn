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
