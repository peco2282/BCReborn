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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

public record RefineryRecipe(
  ResourceLocation id,
  Ingredient primary,
  Optional<Ingredient> secondary,
  FluidStack result,
  int energy,
  int delay
) {
  public static final Codec<RefineryRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("id").forGetter(RefineryRecipe::id),
    Codecs.INGREDIENT_CODEC.fieldOf("primary").forGetter(RefineryRecipe::primary),
    Codecs.INGREDIENT_CODEC.optionalFieldOf("secondary").forGetter(RefineryRecipe::secondary),
    FluidStack.CODEC.fieldOf("result").forGetter(RefineryRecipe::result),
    Codec.INT.fieldOf("energy").forGetter(RefineryRecipe::energy),
    Codec.INT.fieldOf("delay").forGetter(RefineryRecipe::delay)
  ).apply(instance, RefineryRecipe::new));
}