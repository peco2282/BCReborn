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
package com.peco2282.bcreborn.common.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipe;
import com.peco2282.bcreborn.api.recipes.IntegrationRecipe;
import com.peco2282.bcreborn.api.recipes.ProgrammingRecipe;
import com.peco2282.bcreborn.api.recipes.RefineryRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = BCRebornCore.MODID)
public final class RecipeLoaders {
  private static final Logger LOGGER = BCReborn.createLogger();

  public static class AssemblyRecipeLoader extends SimpleJsonResourceReloadListener {
    private Map<ResourceLocation, AssemblyRecipe> recipes = Map.of();

    public AssemblyRecipeLoader() {
      super(new Gson(), "assembly");
    }

    @Override
    protected void apply(
      Map<ResourceLocation, JsonElement> jsons,
      ResourceManager resourceManager,
      ProfilerFiller profiler
    ) {
      var loadedRecipes = new HashMap<ResourceLocation, AssemblyRecipe>();

      for (var entry : jsons.entrySet()) {
        loadedRecipes.put(entry.getKey(), parse(AssemblyRecipe.CODEC, entry.getValue()));
      }

      this.recipes = Map.copyOf(loadedRecipes);
    }

    public Collection<AssemblyRecipe> getRecipes() {
      return recipes.values();
    }

    public @Nullable AssemblyRecipe getRecipe(ResourceLocation id) {
      return recipes.get(id);
    }
  }

  public static class IntegrationRecipeLoader extends SimpleJsonResourceReloadListener {
    private Map<ResourceLocation, IntegrationRecipe> recipes = Map.of();

    public IntegrationRecipeLoader() {
      super(new Gson(), "integration");
    }

    @Override
    protected void apply(
      Map<ResourceLocation, JsonElement> jsons,
      ResourceManager resourceManager,
      ProfilerFiller profiler
    ) {
      var loadedRecipes = new HashMap<ResourceLocation, IntegrationRecipe>();

      for (var entry : jsons.entrySet()) {
        loadedRecipes.put(entry.getKey(), parse(IntegrationRecipe.CODEC, entry.getValue()));
      }

      this.recipes = Map.copyOf(loadedRecipes);
    }

    public Collection<IntegrationRecipe> getRecipes() {
      return recipes.values();
    }

    public @Nullable IntegrationRecipe getRecipe(ResourceLocation id) {
      return recipes.get(id);
    }
  }
  public static class ProgrammingRecipeLoader extends SimpleJsonResourceReloadListener {
    private Map<ResourceLocation, ProgrammingRecipe> recipes = Map.of();

    public ProgrammingRecipeLoader() {
      super(new Gson(), "programming");
    }

    @Override
    protected void apply(
      Map<ResourceLocation, JsonElement> jsons,
      ResourceManager resourceManager,
      ProfilerFiller profiler
    ) {
      var loadedRecipes = new HashMap<ResourceLocation, ProgrammingRecipe>();

      for (var entry : jsons.entrySet()) {
        loadedRecipes.put(entry.getKey(), parse(ProgrammingRecipe.CODEC, entry.getValue()));
      }

      this.recipes = Map.copyOf(loadedRecipes);
    }

    public Collection<ProgrammingRecipe> getRecipes() {
      return recipes.values();
    }

    public @Nullable ProgrammingRecipe getRecipe(ResourceLocation id) {
      return recipes.get(id);
    }
  }
  public static class RefineryRecipeLoader extends SimpleJsonResourceReloadListener {
    private Map<ResourceLocation, RefineryRecipe> recipes = Map.of();

    public RefineryRecipeLoader() {
      super(new Gson(), "refinery");
    }

    @Override
    protected void apply(
      Map<ResourceLocation, JsonElement> jsons,
      ResourceManager resourceManager,
      ProfilerFiller profiler
    ) {
      var loadedRecipes = new HashMap<ResourceLocation, RefineryRecipe>();

      for (var entry : jsons.entrySet()) {
        loadedRecipes.put(entry.getKey(), parse(RefineryRecipe.CODEC, entry.getValue()));
      }

      this.recipes = Map.copyOf(loadedRecipes);
    }

    public Collection<RefineryRecipe> getRecipes() {
      return recipes.values();
    }

    public @Nullable RefineryRecipe getRecipe(ResourceLocation id) {
      return recipes.get(id);
    }
  }

  private static <T> T parse(Codec<T> codec, JsonElement json) {
    return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, LOGGER::error);
  }

  @SubscribeEvent
  public static void onAddReloadListener(AddReloadListenerEvent event) {
    LOGGER.info("Registering recipe loaders");
    event.addListener(new AssemblyRecipeLoader());
    event.addListener(new IntegrationRecipeLoader());
    event.addListener(new ProgrammingRecipeLoader());
    event.addListener(new RefineryRecipeLoader());
    LOGGER.info("Registered recipe loaders");
  }
}
