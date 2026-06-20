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

/**
 * Represents a programming recipe used in BuildCraft Reborn.
 * <p>
 * A programming recipe defines the transformation of an input item combined with an optional
 * programming option into a programmed result item. This process requires a specified amount
 * of energy to complete.
 * <p>
 * This record implements {@link BCRecipe} and provides codec-based serialization support
 * for data-driven recipe definitions.
 */
public record ProgrammingRecipe(
  // The unique identifier for this recipe.
  ResourceLocation id,
  // The input ingredient that will be programmed.
  Ingredient input,
  // The optional programming ingredient that defines the programming behavior.
  Ingredient option,
  // The resulting programmed item stack.
  ItemStack result,
  // The amount of energy required to complete this programming recipe.
  int energy
) implements BCRecipe {
  /**
   * Codec for serializing and deserializing ProgrammingRecipe instances.
   * <p>
   * This codec handles the conversion between ProgrammingRecipe objects and their
   * data representation, enabling data-driven recipe definitions.
   */
  public static final Codec<ProgrammingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    ResourceLocation.CODEC.fieldOf("id").forGetter(ProgrammingRecipe::id),
    Codecs.INGREDIENT_CODEC.fieldOf("input").forGetter(ProgrammingRecipe::input),
    Codecs.INGREDIENT_CODEC.fieldOf("option").forGetter(ProgrammingRecipe::option),
    ItemStack.CODEC.fieldOf("result").forGetter(ProgrammingRecipe::result),
    Codec.INT.fieldOf("energy").forGetter(ProgrammingRecipe::energy)
  ).apply(instance, ProgrammingRecipe::new));
}