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

import java.util.List;

public record AssemblyRecipe(
  ResourceLocation id,
  List<Ingredient> ingredients,
  ItemStack result,
  int energy,
  int craftingTime
) {
  public static final Codec<AssemblyRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("id").forGetter(AssemblyRecipe::id),
    Codecs.INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter(AssemblyRecipe::ingredients),
    ItemStack.CODEC.fieldOf("result").forGetter(AssemblyRecipe::result),
    Codec.INT.fieldOf("energy").forGetter(AssemblyRecipe::energy),
    Codec.INT.fieldOf("crafting_time").forGetter(AssemblyRecipe::craftingTime)
  ).apply(instance, AssemblyRecipe::new));
}