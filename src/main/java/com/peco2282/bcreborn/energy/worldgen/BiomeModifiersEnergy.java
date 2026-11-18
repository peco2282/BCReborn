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
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

/** Oil wells and water springs in every overworld biome (the original skipped the Nether and the End). */
public class BiomeModifiersEnergy {
  public static final ResourceKey<BiomeModifier> OIL_WELL = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, BCRebornEnergy.location("add_oil_well"));
  public static final ResourceKey<BiomeModifier> WATER_SPRING = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, BCRebornCore.location("add_water_spring"));

  public static void bootstrap(BootstapContext<BiomeModifier> context) {
    HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
    HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);
    context.register(OIL_WELL, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
      HolderSet.direct(placed.getOrThrow(PlacedFeaturesEnergy.OIL_WELL)), GenerationStep.Decoration.LAKES));
    context.register(WATER_SPRING, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
      HolderSet.direct(placed.getOrThrow(PlacedFeaturesEnergy.WATER_SPRING)), GenerationStep.Decoration.FLUID_SPRINGS));
  }
}
