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
package com.peco2282.bcreborn.common.data;

import com.mojang.logging.LogUtils;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.builders.data.BuildersBlockStateProvider;
import com.peco2282.bcreborn.core.data.CoreItemModelProvider;
import com.peco2282.bcreborn.core.data.CoreRecipeProvider;
import com.peco2282.bcreborn.core.data.CoreBlockStateProvider;
import com.peco2282.bcreborn.energy.data.EnergyBlockStateProvider;
import com.peco2282.bcreborn.energy.data.EnergyItemModelProvider;
import com.peco2282.bcreborn.transport.data.TransportBlockStateProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

public class DataGatherEvent {
  private static final Logger LOGGER = LogUtils.getLogger();

  @SubscribeEvent
  public static void onGatherData(GatherDataEvent event) {
    LOGGER.info("Gathering data...");
    DataGenerator generator = event.getGenerator();
    ExistingFileHelper helper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
    BCBlockTagsProvider provider = new BCBlockTagsProvider(generator.getPackOutput(), lookup, "bcreborncore", helper);
    generator.addProvider(true, provider);
    generator.addProvider(true, new BCItemTagsProvider(generator.getPackOutput(), lookup, provider.contentsGetter(), "bcreborncore", helper));
    generator.addProvider(true, new BCLanguageProvider(generator.getPackOutput(), "bcreborncore", "en_us"));
    generator.addProvider(true, new BCBlockStateProvider(generator.getPackOutput(), BCReborn.MOD_ID_BASE, helper));
    generator.addProvider(true, new ResourceDataGenerator(generator.getPackOutput(), lookup));

    generator.addProvider(true, new CoreBlockStateProvider(generator.getPackOutput(), helper));
    generator.addProvider(true, new CoreItemModelProvider(generator.getPackOutput(), helper));
    generator.addProvider(true, new CoreRecipeProvider(generator.getPackOutput()));

    generator.addProvider(true, new BuildersBlockStateProvider(generator.getPackOutput(), helper));

    generator.addProvider(true, new EnergyBlockStateProvider(generator.getPackOutput(), helper));;
    generator.addProvider(true, new EnergyItemModelProvider(generator.getPackOutput(), helper));

    generator.addProvider(true, new TransportBlockStateProvider(generator.getPackOutput(), helper));
  }
}
