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
package com.peco2282.bcreborn.core.item;

import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.core.util.IWrench;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class WrenchItem extends BuildCraftItem implements IWrench {
  public WrenchItem(Item.Properties properties) {
    super(properties);
  }

  @Override
  public boolean canWrench(Player player, int x, int y, int z) {
    return true;
  }

  @Override
  public void wrenchUsed(Player player, int x, int y, int z) {
  }
}
