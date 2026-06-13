package com.peco2282.bcreborn.api.recipes;

import net.minecraft.resources.ResourceLocation;

public abstract class BCRecipeBuilder<T extends BCRecipe> {
  protected final ResourceLocation id;

  public BCRecipeBuilder(ResourceLocation id) {
    this.id = id;
  }

  public abstract T build();
}
