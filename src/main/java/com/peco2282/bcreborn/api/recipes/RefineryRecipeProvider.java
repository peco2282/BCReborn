package com.peco2282.bcreborn.api.recipes;

import net.minecraft.data.PackOutput;

public abstract class RefineryRecipeProvider extends BCRecipeProvider<RefineryRecipe, RefineryRecipeBuilder> {
  public static final String DIRECTORY = kind("refinery");
  private final String modId;

  public RefineryRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), RefineryRecipe.CODEC);
    this.modId = modId;
  }

  @Override
  public String getName() {
    return "Refinery recipes for " + modId;
  }
}
