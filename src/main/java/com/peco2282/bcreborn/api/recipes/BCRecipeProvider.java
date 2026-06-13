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

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class BCRecipeProvider<R extends BCRecipe, B extends BCRecipeBuilder<R>> implements DataProvider {
  protected final PackOutput.PathProvider output;
  private final Codec<R> codec;
  protected final ArrayList<R> recipes = new ArrayList<>();

  protected static String kind(String type) {
    return "bcreborn/" + type;
  }

  public BCRecipeProvider(PackOutput.PathProvider output, Codec<R> codec) {
    this.output = output;
    this.codec = codec;
  }

  public abstract void create();

  @Override
  public CompletableFuture<?> run(CachedOutput p_236071_) {
    var futures = new ArrayList<CompletableFuture<?>>();
    var done = new HashSet<ResourceLocation>();
    create();
    for (R recipe : recipes) {
      JsonElement element = codec.encodeStart(JsonOps.INSTANCE, recipe).result().orElseThrow();
      futures.add(DataProvider.saveStable(p_236071_, element, output.json(recipe.id())));
    }

    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
  }

  protected final void addRecipe(R recipe) {
    recipes.add(recipe);
  }

  @SafeVarargs
  protected final void addRecipes(R... recipes) {
    for (R r : recipes) {
      addRecipe(r);
    }
  }

  protected final void addRecipes(List<R> recipes) {
    for (R r : recipes) {
      addRecipe(r);
    }
  }

  protected final void addRecipe(B builder) {
    addRecipe(builder.build());
  }

  protected final void addRecipe(Supplier<R> recipe) {
    addRecipe(recipe.get());
  }
}
