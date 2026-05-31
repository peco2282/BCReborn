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
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;
public class PlacedFeaturesEnergy {

  public static final ResourceKey<PlacedFeature> OIL_LAKE_DESERT =
      ResourceKey.create(
          Registries.PLACED_FEATURE,
          BCRebornEnergy.location("oil_lake_desert")
      );
  public static final ResourceKey<PlacedFeature> OIL_LAKE_OCEAN =
      ResourceKey.create(
          Registries.PLACED_FEATURE,
          BCRebornEnergy.location("oil_lake_ocean")
      );

  public static void bootstrap(BootstapContext<PlacedFeature> context) {

    HolderGetter<ConfiguredFeature<?, ?>> configured =
        context.lookup(Registries.CONFIGURED_FEATURE);

    context.register(
        OIL_LAKE_DESERT,
        new PlacedFeature(
            configured.getOrThrow(ConfiguredFeaturesEnergy.OIL_LAKE),
            List.of(
                BiomeFilter.biome(),
                NoiseThresholdCountPlacement.of(0.7, 0, 1),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE_WG)
            )
        )
    );
    context.register(
        OIL_LAKE_OCEAN,
        new PlacedFeature(
            configured.getOrThrow(ConfiguredFeaturesEnergy.OIL_LAKE),
            List.of(
                BiomeFilter.biome(),
                NoiseThresholdCountPlacement.of(0.9, 0, 1),
                InSquarePlacement.spread(),
                HeightmapPlacement.onHeightmap(Heightmap.Types.OCEAN_FLOOR_WG)
            )
        )
    );
  }

}
