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
package com.peco2282.bcreborn.energy.worldgen;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.core.worldgen.WaterSpringFeature;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

/**
 * Placement: the oil well feature gets one try per chunk and rolls the original chances itself; a water spring comes
 * in about one chunk of 40.
 */
public class PlacedFeaturesEnergy {
  public static final ResourceKey<PlacedFeature> OIL_WELL = ResourceKey.create(Registries.PLACED_FEATURE, BCRebornEnergy.location("oil_well"));
  public static final ResourceKey<PlacedFeature> WATER_SPRING = ResourceKey.create(Registries.PLACED_FEATURE, BCRebornCore.location("water_spring"));

  public static void bootstrap(BootstapContext<PlacedFeature> context) {
    HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
    context.register(OIL_WELL, new PlacedFeature(configured.getOrThrow(ConfiguredFeaturesEnergy.OIL_WELL),
      List.of(InSquarePlacement.spread(), BiomeFilter.biome())));
    context.register(WATER_SPRING, new PlacedFeature(configured.getOrThrow(ConfiguredFeaturesEnergy.WATER_SPRING),
      List.of(RarityFilter.onAverageOnceEvery(WaterSpringFeature.CHUNKS_PER_SPRING), InSquarePlacement.spread(), BiomeFilter.biome())));
  }
}
