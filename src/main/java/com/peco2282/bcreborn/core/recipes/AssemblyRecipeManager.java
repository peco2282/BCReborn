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
import com.peco2282.bcreborn.BCReborn;
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
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = BCRebornCore.MODID)
public class AssemblyRecipeManager implements IAssemblyRecipeManager {
  public static final AssemblyRecipeManager INSTANCE = new AssemblyRecipeManager();
  private static final Logger LOGGER = BCReborn.createLogger();
  private final Map<ResourceLocation, AssemblyRecipe> recipes = new ConcurrentHashMap<>();

  private AssemblyRecipeManager() {
  }

  @SubscribeEvent
  public static void onAddReloadListener(AddReloadListenerEvent event) {
    LOGGER.info("Registering Assembly recipe loaders");
    event.addListener(new AssemblyRecipeLoader());
    LOGGER.info("Registered Assembly recipe loaders");
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
    }
  }
}
