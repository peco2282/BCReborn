package com.peco2282.bcreborn.energy.worldgen;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.energy.FluidsEnergy;
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
                    FluidsEnergy.OIL_BLOCK.get()
                ),
                BlockStateProvider.simple(
                    Blocks.SAND
                )
            )
        )
    );
  }
}
