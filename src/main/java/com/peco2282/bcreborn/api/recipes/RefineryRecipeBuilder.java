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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RefineryRecipeBuilder extends BCRecipeBuilder<RefineryRecipe> {
  private Ingredient primary;
  private @Nullable Ingredient secondary;
  private FluidStack result;
  private int energy;
  private int delay;

  private RefineryRecipeBuilder(ResourceLocation id) {
    super(id);
  }

  public RefineryRecipeBuilder setPrimary(Ingredient primary) {
    this.primary = primary;
    return this;
  }

  public RefineryRecipeBuilder setSecondary(@Nullable Ingredient secondary) {
    this.secondary = secondary;
    return this;
  }

  public RefineryRecipeBuilder setResult(FluidStack result) {
    this.result = result;
    return this;
  }

  public RefineryRecipeBuilder setEnergy(int energy) {
    this.energy = energy;
    return this;
  }

  public RefineryRecipeBuilder setDelay(int delay) {
    this.delay = delay;
    return this;
  }

  @Override
  public RefineryRecipe build() {
    return new RefineryRecipe(id, primary, Optional.ofNullable(secondary), result, energy, delay);
  }
}
