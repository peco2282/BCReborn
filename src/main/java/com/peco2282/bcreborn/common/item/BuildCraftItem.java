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
package com.peco2282.bcreborn.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;

public class BuildCraftItem extends Item {
  private boolean passSneakClick;

  public BuildCraftItem(Properties properties) {
    super(properties);
  }

  public BuildCraftItem() {
    this(new Properties());
  }


  public Item setPassSneakClick(boolean passClick) {
    this.passSneakClick = passClick;
    return this;
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
    return passSneakClick;
  }
}
