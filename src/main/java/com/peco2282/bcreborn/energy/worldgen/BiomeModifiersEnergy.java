package com.peco2282.bcreborn.energy.worldgen;

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

public class BiomeModifiersEnergy {
  public static final ResourceKey<BiomeModifier> OIL_LAKE = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, BCRebornEnergy.location("add_oil_lake"));

  public static void bootstrap(BootstapContext<BiomeModifier> context) {
    HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
    HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);

    context.register(
        OIL_LAKE,
        new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.HAS_DESERT_PYRAMID),
            HolderSet.direct(
                placedFeatures.getOrThrow(PlacedFeaturesEnergy.OIL_LAKE_PLACED)
            ),
            GenerationStep.Decoration.LAKES
        )
    );
  }
}
