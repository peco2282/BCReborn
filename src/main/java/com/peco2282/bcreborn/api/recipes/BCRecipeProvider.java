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

/**
 * Abstract base class for providing BuildCraft recipes during data generation.
 * <p>
 * This class handles the serialization and saving of custom BuildCraft recipes
 * to JSON files using Minecraft's data generation system.
 *
 * @param <R> The type of recipe this provider generates, must extend {@link BCRecipe}.
 * @param <B> The type of recipe builder used to construct recipes, must extend {@link BCRecipeBuilder}.
 */
public abstract class BCRecipeProvider<R extends BCRecipe, B extends BCRecipeBuilder<R>> implements DataProvider {
  protected final PackOutput.PathProvider output;
  protected final ArrayList<R> recipes = new ArrayList<>();
  protected final String modId;
  private final Codec<R> codec;

  /**
   * Constructs a new recipe provider.
   *
   * @param output The path provider for determining output file locations.
   * @param codec  The codec used for serializing recipes to JSON.
   * @param modId  The mod ID used for creating resource locations.
   */
  public BCRecipeProvider(PackOutput.PathProvider output, Codec<R> codec, String modId) {
    this.output = output;
    this.codec = codec;
    this.modId = modId;
  }

  /**
   * Creates a recipe type path prefixed with "bcreborn/".
   *
   * @param type The recipe type name.
   * @return The full recipe type path.
   */
  protected static String kind(String type) {
    return "bcreborn/" + type;
  }

  /**
   * Creates and registers all recipes for this provider.
   * <p>
   * This method should be implemented by subclasses to define their recipes
   * using the various addRecipe() methods.
   */
  public abstract void create();

  /**
   * Runs the data generation process, creating and saving all recipes.
   *
   * @param p_236071_ The cached output for data generation.
   * @return A CompletableFuture that completes when all recipes have been saved.
   */
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

  /**
   * Adds a single recipe to this provider.
   *
   * @param recipe The recipe to add.
   */
  protected final void addRecipe(R recipe) {
    recipes.add(recipe);
  }

  /**
   * Adds multiple recipes to this provider.
   *
   * @param recipes The recipes to add.
   */
  @SafeVarargs
  protected final void addRecipes(R... recipes) {
    for (R r : recipes) {
      addRecipe(r);
    }
  }

  /**
   * Adds multiple recipes from a list to this provider.
   *
   * @param recipes The list of recipes to add.
   */
  protected final void addRecipes(List<R> recipes) {
    for (R r : recipes) {
      addRecipe(r);
    }
  }

  /**
   * Builds and adds a recipe from a builder.
   *
   * @param builder The recipe builder to build and add.
   */
  protected final void addRecipe(B builder) {
    addRecipe(builder.build());
  }

  /**
   * Adds a recipe from a supplier.
   *
   * @param recipe The supplier that provides the recipe.
   */
  protected final void addRecipe(Supplier<R> recipe) {
    addRecipe(recipe.get());
  }

  /**
   * Creates a ResourceLocation using this provider's mod ID.
   *
   * @param path The path component of the resource location.
   * @return A ResourceLocation with this provider's mod ID and the given path.
   */
  protected ResourceLocation loc(String path) {
    return ResourceLocation.fromNamespaceAndPath(modId, path);
  }
}
