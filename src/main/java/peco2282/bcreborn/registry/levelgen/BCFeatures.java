package peco2282.bcreborn.registry.levelgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.block.BCCoreBlocks;

public class BCFeatures {
  public static final ResourceKey<ConfiguredFeature<?, ?>> OIL_SOURCE = ResourceKey.create(Registries.CONFIGURED_FEATURE, BCReborn.location("oil_source"));

  public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
    FeatureUtils.register(
        context,
        OIL_SOURCE,
        Feature.LAKE,
        new LakeFeature.Configuration(BlockStateProvider.simple(BCCoreBlocks.OIL_SOURCE.get()), BlockStateProvider.simple(Blocks.STONE))
    );
  }
}
