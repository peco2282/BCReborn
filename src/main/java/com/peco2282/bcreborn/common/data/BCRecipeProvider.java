package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.builders.data.BuildersRecipe;
import com.peco2282.bcreborn.core.data.CoreRecipe;
import com.peco2282.bcreborn.energy.data.EnergyRecipe;
import com.peco2282.bcreborn.factory.data.FactoryRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.function.Consumer;

public class BCRecipeProvider extends RecipeProvider {
  public BCRecipeProvider(PackOutput p_248933_) {
    super(p_248933_);
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> p_251297_) {
    CoreRecipe.build(p_251297_);
    BuildersRecipe.build(p_251297_);
    EnergyRecipe.build(p_251297_);
    FactoryRecipe.build(p_251297_);
  }
}
