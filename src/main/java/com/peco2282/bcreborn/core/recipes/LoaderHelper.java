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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.api.recipes.BCRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class LoaderHelper<R extends BCRecipe> extends SimpleJsonResourceReloadListener {
  private static final Logger LOGGER = BCReborn.createLogger();

  protected LoaderHelper(String p_10769_) {
    super(new Gson(), p_10769_);
  }

  protected void doReload(Map<ResourceLocation, JsonElement> jsons, ProfilerFiller profiler, Codec<R> codec, Map<ResourceLocation, R> recipes) {
    var map = new HashMap<ResourceLocation, R>();
    profiler.push("recipes");
    jsons.forEach((id, json) -> map.put(id, parse(codec, json)));
    recipes.clear();
    recipes.putAll(map);
    profiler.pop();
  }

  protected R parse(Codec<R> codec, JsonElement json) {
    return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, LOGGER::error);
  }
}
