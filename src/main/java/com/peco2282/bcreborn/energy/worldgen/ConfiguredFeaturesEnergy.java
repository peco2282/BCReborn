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

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.energy.EnergyFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ConfiguredFeaturesEnergy {
  public static final ResourceKey<ConfiguredFeature<?, ?>> OIL_LAKE = ResourceKey.create(Registries.CONFIGURED_FEATURE, BCRebornEnergy.location("oil_lake"));

  public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
    context.register(
      OIL_LAKE,
      new ConfiguredFeature<>(
        Feature.LAKE,
        new LakeFeature.Configuration(
          BlockStateProvider.simple(
            EnergyFluids.OIL_BLOCK.get()
          ),
          BlockStateProvider.simple(
            Blocks.SAND
          )
        )
      )
    );
  }
}
