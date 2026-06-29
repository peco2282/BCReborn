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
package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.BCReborn;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.Set;

public class BCBlockLoot extends BlockLootSubProvider {
  protected BCBlockLoot() {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags());
  }

  @Override
  protected void generate() {
    // bcrebornで始まる全ブロックを自動的にドロップ対象にする
    for (Block block : getKnownBlocks()) {
      if (block.asItem() != Items.AIR) {
        this.dropSelf(block);
      }
    }
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ForgeRegistries.BLOCKS.getEntries().stream()
      .filter(entry -> entry.getKey().location().getNamespace().startsWith(BCReborn.MOD_ID_BASE))
      .map(Map.Entry::getValue)
      .toList();
  }
}
