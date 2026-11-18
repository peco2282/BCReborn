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
package com.peco2282.bcreborn.core.recipes;

import com.google.gson.JsonElement;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipe;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipeProvider;
import com.peco2282.bcreborn.api.recipes.IAssemblyRecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = BCRebornCore.MODID)
public final class AssemblyRecipeManager implements IAssemblyRecipeManager {
  public static final AssemblyRecipeManager INSTANCE = new AssemblyRecipeManager();
  private static final AssemblyRecipeLoader LOADER = new AssemblyRecipeLoader();
  private final Map<ResourceLocation, AssemblyRecipe> recipes = new ConcurrentHashMap<>();

  /** Recipes that are made from the registries rather than read from data packs (original IFlexibleRecipe). */
  private static final List<Supplier<Collection<AssemblyRecipe>>> DYNAMIC_SOURCES = new CopyOnWriteArrayList<>();

  private AssemblyRecipeManager() {
  }

  /**
   * Adds recipes that are computed on every reload, after the data pack recipes (for instance one facade recipe per
   * block). A data pack recipe with the same id wins.
   */
  public static void addDynamicSource(Supplier<Collection<AssemblyRecipe>> source) {
    DYNAMIC_SOURCES.add(source);
  }

  @SubscribeEvent
  public static void onAddReloadListener(AddReloadListenerEvent event) {
    event.addListener(LOADER);
  }

  @Override
  public @Nullable AssemblyRecipe getRecipe(ResourceLocation id) {
    return recipes.get(id);
  }

  @Override
  public Collection<AssemblyRecipe> getRecipes() {
    return Collections.unmodifiableCollection(recipes.values());
  }

  @Override
  public boolean contains(ResourceLocation id) {
    return recipes.containsKey(id);
  }

  private static class AssemblyRecipeLoader extends LoaderHelper<AssemblyRecipe> {
    public AssemblyRecipeLoader() {
      super(AssemblyRecipeProvider.DIRECTORY);
    }

    @Override
    protected void apply(
      Map<ResourceLocation, JsonElement> jsons,
      ResourceManager resourceManager,
      ProfilerFiller profiler
    ) {
      doReload(jsons, profiler, AssemblyRecipe.CODEC, INSTANCE.recipes);
      for (Supplier<Collection<AssemblyRecipe>> source : DYNAMIC_SOURCES) {
        for (AssemblyRecipe recipe : source.get()) {
          INSTANCE.recipes.putIfAbsent(recipe.id(), recipe);
        }
      }
    }
  }
}
