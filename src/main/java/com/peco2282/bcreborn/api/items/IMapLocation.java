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
package com.peco2282.bcreborn.api.items;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.IZone;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IMapLocation extends INamedItem {
  MapLocationType getType(ItemStack stack);

  BlockIndex getPoint(ItemStack stack);

  IBox getBox(ItemStack stack);

  IZone getZone(ItemStack stack);

  List<BlockIndex> getPath(ItemStack stack);

  Direction getPointSide(ItemStack stack);

  enum MapLocationType {
    CLEAN, SPOT, AREA, PATH, ZONE
  }
}
