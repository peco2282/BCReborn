/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.registry.levelgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import peco2282.bcreborn.BCReborn;

import java.util.List;

public class BCPlacements {
  public static final ResourceKey<PlacedFeature> OIL_SOURCE =
      ResourceKey.create(Registries.PLACED_FEATURE, BCReborn.location("oil_source"));

  public static void bootstrap(BootstrapContext<PlacedFeature> context) {
    var source = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(BCFeatures.OIL_SOURCE);
    PlacementUtils.register(
        context,
        OIL_SOURCE,
        source,
        List.of(
            RarityFilter.onAverageOnceEvery(20),
            HeightRangePlacement.uniform(
                VerticalAnchor.belowTop(50), VerticalAnchor.aboveBottom(100)),
            OilPlacementFilter.filter()));
  }
}
