package com.peco2282.bcreborn.api.recipes;

import net.minecraft.data.PackOutput;

public abstract class ProgrammingRecipeProvider extends BCRecipeProvider<ProgrammingRecipe, ProgrammingRecipeBuilder> {
  public static final String DIRECTORY = kind("programming");
  private final String modId;

  public ProgrammingRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), ProgrammingRecipe.CODEC);
    this.modId = modId;
  }

  @Override
  public String getName() {
    return "Programming recipes for " + modId;
  }
}
