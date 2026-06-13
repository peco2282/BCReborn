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
import com.peco2282.bcreborn.api.recipes.IProgrammingRecipeManager;
import com.peco2282.bcreborn.api.recipes.ProgrammingRecipe;
import com.peco2282.bcreborn.api.recipes.ProgrammingRecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = BCRebornCore.MODID)
public final class ProgrammingRecipeManager implements IProgrammingRecipeManager {
  public static final ProgrammingRecipeManager INSTANCE = new ProgrammingRecipeManager();
  private static final ProgrammingRecipeLoader LOADER = new ProgrammingRecipeLoader();
  private final Map<ResourceLocation, ProgrammingRecipe> recipes = new ConcurrentHashMap<>();

  private ProgrammingRecipeManager() {
  }

  @SubscribeEvent
  public static void onAddReloadListener(AddReloadListenerEvent event) {
    event.addListener(LOADER);
  }

  @Override
  public @Nullable ProgrammingRecipe getRecipe(ResourceLocation id) {
    return recipes.get(id);
  }

  @Override
  public Collection<ProgrammingRecipe> getRecipes() {
    return Collections.unmodifiableCollection(recipes.values());
  }

  @Override
  public boolean contains(ResourceLocation id) {
    return recipes.containsKey(id);
  }

  private static class ProgrammingRecipeLoader extends LoaderHelper<ProgrammingRecipe> {
    public ProgrammingRecipeLoader() {
      super(ProgrammingRecipeProvider.DIRECTORY);
    }

    @Override
    protected void apply(
      Map<ResourceLocation, JsonElement> jsons,
      ResourceManager resourceManager,
      ProfilerFiller profiler
    ) {
      doReload(jsons, profiler, ProgrammingRecipe.CODEC, INSTANCE.recipes);
    }
  }
}
