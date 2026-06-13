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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ProgrammingRecipe(
  ResourceLocation id,
  Ingredient input,
  Ingredient option,
  ItemStack result,
  int energy
) implements BCRecipe {
  public static final Codec<ProgrammingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("id").forGetter(ProgrammingRecipe::id),
    Codecs.INGREDIENT_CODEC.fieldOf("input").forGetter(ProgrammingRecipe::input),
    Codecs.INGREDIENT_CODEC.fieldOf("option").forGetter(ProgrammingRecipe::option),
    ItemStack.CODEC.fieldOf("result").forGetter(ProgrammingRecipe::result),
    Codec.INT.fieldOf("energy").forGetter(ProgrammingRecipe::energy)
  ).apply(instance, ProgrammingRecipe::new));
}