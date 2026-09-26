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

/**
 * Represents a refinery recipe in BuildCraft Reborn.
 * <p>
 * A refinery recipe defines how fluids are processed in a refinery, including the primary
 * and optional secondary ingredients, the resulting fluid output, energy requirements,
 * and processing delay.
 *
 * @param id        The unique identifier for this recipe.
 * @param primary   Bucket ingredient identifying the primary fluid (the bucket itself is not consumed).
 * @param secondary Optional bucket ingredient identifying the second fluid.
 * @param result    The fluid stack produced by this recipe.
 * @param energy    The energy cost (in RF/FE) required to process this recipe.
 * @param delay     The processing time delay (in ticks) for this recipe.
 * @param primaryAmount Primary fluid consumed per operation, in mB.
 * @param secondaryAmount Secondary fluid consumed per operation, in mB.
 */
public record RefineryRecipe(
  ResourceLocation id,
  Ingredient primary,
  Optional<Ingredient> secondary,
  FluidStack result,
  int energy,
  int delay,
  int primaryAmount,
  int secondaryAmount
) implements BCRecipe {
  public RefineryRecipe(ResourceLocation id, Ingredient primary, Optional<Ingredient> secondary,
                        FluidStack result, int energy, int delay) {
    this(id, primary, secondary, result, energy, delay, 1, 1);
  }
  /**
   * Codec for serializing and deserializing RefineryRecipe instances.
   */
  public static final Codec<RefineryRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("id").forGetter(RefineryRecipe::id),
    Codecs.INGREDIENT_CODEC.fieldOf("primary").forGetter(RefineryRecipe::primary),
    Codecs.INGREDIENT_CODEC.optionalFieldOf("secondary").forGetter(RefineryRecipe::secondary),
    FluidStack.CODEC.fieldOf("result").forGetter(RefineryRecipe::result),
    Codec.intRange(0, Integer.MAX_VALUE).fieldOf("energy").forGetter(RefineryRecipe::energy),
    Codec.intRange(1, Integer.MAX_VALUE).fieldOf("delay").forGetter(RefineryRecipe::delay),
    Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("primary_amount", 1).forGetter(RefineryRecipe::primaryAmount),
    Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("secondary_amount", 1).forGetter(RefineryRecipe::secondaryAmount)
  ).apply(instance, RefineryRecipe::new));
}
