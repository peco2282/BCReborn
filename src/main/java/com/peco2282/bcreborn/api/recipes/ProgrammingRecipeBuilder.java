package com.peco2282.bcreborn.api.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ProgrammingRecipeBuilder extends BCRecipeBuilder<ProgrammingRecipe> {
  private Ingredient input;
  private Ingredient option;
  private ItemStack result;
  private int energy;

  public ProgrammingRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  public ProgrammingRecipeBuilder setInput(Ingredient input) {
    this.input = input;
    return this;
  }

  public ProgrammingRecipeBuilder setOption(Ingredient option) {
    this.option = option;
    return this;
  }

  public ProgrammingRecipeBuilder setResult(ItemStack result) {
    this.result = result;
    return this;
  }

  public ProgrammingRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  public static ProgrammingRecipeBuilder create(ResourceLocation id) {
    return new ProgrammingRecipeBuilder(id);
  }

  @Override
  public ProgrammingRecipe build() {
    return new ProgrammingRecipe(id, input, option, result, energy);
  }
}
