package com.peco2282.bcreborn.api.recipes;

import net.minecraft.data.PackOutput;

public abstract class AssemblyRecipeProvider extends BCRecipeProvider<AssemblyRecipe, AssemblyRecipeBuilder> {
  public static final String DIRECTORY = kind("assembly");
  private final String modId;

  public AssemblyRecipeProvider(PackOutput output, String modId) {
    super(output.createPathProvider(PackOutput.Target.DATA_PACK, DIRECTORY), AssemblyRecipe.CODEC);
    this.modId = modId;
  }

  @Override
  public String getName() {
    return "Assembly recipes for " + modId;
  }
}
