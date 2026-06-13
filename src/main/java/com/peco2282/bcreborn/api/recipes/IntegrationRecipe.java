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

public record IntegrationRecipe(
  ResourceLocation id,
  Ingredient input,
  List<Ingredient> expansions,
  ItemStack result,
  int energy,
  int maxExpansionCount
) implements BCRecipe {
  public static final Codec<IntegrationRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("id").forGetter(IntegrationRecipe::id),
    Codecs.INGREDIENT_CODEC.fieldOf("input").forGetter(IntegrationRecipe::input),
    Codecs.INGREDIENT_CODEC.listOf().fieldOf("expansions").forGetter(IntegrationRecipe::expansions),
    ItemStack.CODEC.fieldOf("result").forGetter(IntegrationRecipe::result),
    Codec.INT.fieldOf("energy").forGetter(IntegrationRecipe::energy),
    Codec.INT.fieldOf("max_expansion_count").forGetter(IntegrationRecipe::maxExpansionCount)
  ).apply(instance, IntegrationRecipe::new));
}