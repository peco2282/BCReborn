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
package com.peco2282.bcreborn.core.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

/**
 * Original {@code WorldPropertyIsOre(harvestLevel)}: an ore that can be mined at the given harvest level
 * (0 = wood/gold, 1 = stone, 2 = iron, 3 = diamond). Harvest levels map to the vanilla {@code needs_*_tool} tags.
 */
public class WorldPropertyIsOre extends WorldProperty {
  private final int harvestLevel;

  public WorldPropertyIsOre() {
    this(Integer.MAX_VALUE);
  }

  public WorldPropertyIsOre(int harvestLevel) {
    this.harvestLevel = harvestLevel;
  }

  @Override
  public boolean get(BlockGetter blockAccess, BlockState state, BlockPos pos) {
    return state.is(Tags.Blocks.ORES) && requiredLevel(state) <= harvestLevel;
  }

  private static int requiredLevel(BlockState state) {
    if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) return 3;
    if (state.is(BlockTags.NEEDS_IRON_TOOL)) return 2;
    if (state.is(BlockTags.NEEDS_STONE_TOOL)) return 1;
    return 0;
  }
}
