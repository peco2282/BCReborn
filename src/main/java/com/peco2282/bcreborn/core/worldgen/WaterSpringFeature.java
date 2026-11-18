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
package com.peco2282.bcreborn.core.worldgen;

import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.block.SpringBlock;
import com.peco2282.bcreborn.energy.EnergyConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Water springs of the original {@code SpringPopulate} (one in about 40 chunks, chosen by the placement): a spring
 * block on the bedrock floor, and a water column up through the rock to the first open space.
 */
public class WaterSpringFeature extends Feature<NoneFeatureConfiguration> {
  /** Original: {@code random.nextFloat() > 0.025f} skips the chunk. */
  public static final int CHUNKS_PER_SPRING = 40;

  public WaterSpringFeature() {
    super(NoneFeatureConfiguration.CODEC);
  }

  @Override
  public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
    if (!EnergyConfig.isWorldgenEnable() || !SpringBlock.EnumSpring.WATER.canGen()) return false;
    BlockPos origin = context.origin();
    return placeSpring(context.level(), origin.getX(), origin.getZ(), context.level().getMinBuildHeight());
  }

  /**
   * Original {@code doPopulate()}: looks for bedrock in the five layers from {@code bottom}; the spring replaces the
   * bottom bedrock (or sits on a flat bedrock layer), and the blocks above it turn into water up to the first air.
   */
  public static boolean placeSpring(WorldGenLevel level, int x, int z, int bottom) {
    for (int i = 0; i < 5; i++) {
      if (!level.getBlockState(new BlockPos(x, bottom + i, z)).is(Blocks.BEDROCK)) continue;
      // Handle flat bedrock maps.
      int y = bottom + (i > 0 ? i : i - 1);
      BlockPos springPos = new BlockPos(x, y + 1, z);
      BlockState spring = CoreBlocks.SPRING.get().defaultBlockState().setValue(SpringBlock.TYPE, SpringBlock.EnumSpring.WATER);
      level.setBlock(springPos, spring, 2);
      level.scheduleTick(springPos, spring.getBlock(), SpringBlock.EnumSpring.WATER.getTickRate());
      for (int j = y + 2; j < level.getMaxBuildHeight(); j++) {
        BlockPos above = new BlockPos(x, j, z);
        if (level.isEmptyBlock(above)) break;
        level.setBlock(above, Blocks.WATER.defaultBlockState(), 2);
      }
      return true;
    }
    return false;
  }
}
