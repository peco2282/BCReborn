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

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.energy.worldgen.OilBiomes;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

/** Biome tags for the oil fields: overworld, desert / ocean like the biomes they replace, and excessive oil. */
public class BCBiomeTagsProvider extends BiomeTagsProvider {
  public BCBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper helper) {
    super(output, lookup, BCReborn.MOD_ID_BASE, helper);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(BiomeTags.IS_OVERWORLD).add(OilBiomes.OIL_DESERT, OilBiomes.OIL_OCEAN);
    tag(Tags.Biomes.IS_DESERT).add(OilBiomes.OIL_DESERT);
    tag(Tags.Biomes.IS_SANDY).add(OilBiomes.OIL_DESERT);
    tag(Tags.Biomes.IS_HOT_OVERWORLD).add(OilBiomes.OIL_DESERT);
    tag(Tags.Biomes.IS_DRY_OVERWORLD).add(OilBiomes.OIL_DESERT);
    tag(BiomeTags.IS_OCEAN).add(OilBiomes.OIL_OCEAN);
    tag(Tags.Biomes.IS_WATER).add(OilBiomes.OIL_OCEAN);
    // Original makeBiome(): both oil fields are excessive and surface deposit biomes.
    tag(OilBiomes.EXCESSIVE_OIL).add(OilBiomes.OIL_DESERT, OilBiomes.OIL_OCEAN);
  }
}
