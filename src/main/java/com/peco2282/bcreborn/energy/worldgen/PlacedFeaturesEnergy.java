package com.peco2282.bcreborn.energy.worldgen;
import com.peco2282.bcreborn.BCRebornEnergy;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;
public class PlacedFeaturesEnergy {

  public static final ResourceKey<PlacedFeature> OIL_LAKE_PLACED =
      ResourceKey.create(
          Registries.PLACED_FEATURE,
          BCRebornEnergy.location("oil_lake_placed")
      );

  public static void bootstrap(BootstapContext<PlacedFeature> context) {

    HolderGetter<ConfiguredFeature<?, ?>> configured =
        context.lookup(Registries.CONFIGURED_FEATURE);

    context.register(
        OIL_LAKE_PLACED,
        new PlacedFeature(
            configured.getOrThrow(ConfiguredFeaturesEnergy.OIL_LAKE),
            List.of(
                RarityFilter.onAverageOnceEvery(80),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                    VerticalAnchor.absolute(0),
                    VerticalAnchor.absolute(256)
                ),
                BiomeFilter.biome()
            )
        )
    );
  }

}
