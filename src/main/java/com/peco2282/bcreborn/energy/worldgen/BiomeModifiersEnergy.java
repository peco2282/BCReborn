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
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeModifiersEnergy {
  public static final ResourceKey<BiomeModifier> OIL_LAKE_DESERT = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, BCRebornEnergy.location("add_oil_lake_desert"));
  public static final ResourceKey<BiomeModifier> OIL_LAKE_OCEAN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, BCRebornEnergy.location("add_oil_lake_ocean"));

  public static void bootstrap(BootstapContext<BiomeModifier> context) {
    HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
    HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

    context.register(
        OIL_LAKE_DESERT,
        new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomes.getOrThrow(Tags.Biomes.IS_DESERT),
            HolderSet.direct(
                placedFeatures.getOrThrow(PlacedFeaturesEnergy.OIL_LAKE_DESERT)
            ),
            GenerationStep.Decoration.VEGETAL_DECORATION
        )
    );

    context.register(
        OIL_LAKE_OCEAN,
        new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_OCEAN),
            HolderSet.direct(
                placedFeatures.getOrThrow(PlacedFeaturesEnergy.OIL_LAKE_OCEAN)
            ),
            GenerationStep.Decoration.VEGETAL_DECORATION
        )
    );
  }
}
