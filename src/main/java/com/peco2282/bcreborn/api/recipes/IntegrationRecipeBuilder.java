package com.peco2282.bcreborn.api.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class IntegrationRecipeBuilder extends BCRecipeBuilder<IntegrationRecipe> {
  private Ingredient input;
  private final List<Ingredient> expansions = new ArrayList<>();
  private ItemStack result;
  private int energy;
  private int maxExpansionCount;

  private IntegrationRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  public IntegrationRecipeBuilder setInput(Ingredient input) {
    this.input = input;
    return this;
  }

  public IntegrationRecipeBuilder addExpansion(Ingredient expansion) {
    this.expansions.add(expansion);
    return this;
  }

  public IntegrationRecipeBuilder setResult(ItemStack result) {
    this.result = result;
    return this;
  }

  public IntegrationRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  public IntegrationRecipeBuilder setMaxExpansionCount(int maxExpansionCount) {
    this.maxExpansionCount = maxExpansionCount;
    return this;
  }

  public static IntegrationRecipeBuilder create(ResourceLocation id) {
    return new IntegrationRecipeBuilder(id);
  }

  @Override
  public IntegrationRecipe build() {
    return new IntegrationRecipe(id, input, expansions, result, energy, maxExpansionCount);
  }
}
