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
import com.peco2282.bcreborn.core.worldgen.BCFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/** Configured features for the oil wells (original OilPopulate) and water springs (original SpringPopulate). */
public class ConfiguredFeaturesEnergy {
  public static final ResourceKey<ConfiguredFeature<?, ?>> OIL_WELL = ResourceKey.create(Registries.CONFIGURED_FEATURE, BCRebornEnergy.location("oil_well"));
  public static final ResourceKey<ConfiguredFeature<?, ?>> WATER_SPRING = ResourceKey.create(Registries.CONFIGURED_FEATURE, BCRebornCore.location("water_spring"));

  public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
    context.register(OIL_WELL, new ConfiguredFeature<>(BCFeatures.OIL_WELL.get(), FeatureConfiguration.NONE));
    context.register(WATER_SPRING, new ConfiguredFeature<>(BCFeatures.WATER_SPRING.get(), FeatureConfiguration.NONE));
  }
}
