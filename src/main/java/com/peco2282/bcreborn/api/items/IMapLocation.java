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

/**
 * Interface for items that represent a location or region on a map.
 * Extends {@link INamedItem}.
 */
public interface IMapLocation extends INamedItem {

  /**
   * Gets the type of the map location.
   *
   * @param stack The item stack.
   * @return The {@link MapLocationType}.
   */
  MapLocationType getType(ItemStack stack);

  /**
   * Gets the single point represented by this location.
   *
   * @param stack The item stack.
   * @return The {@link BlockIndex} point.
   */
  BlockIndex getPoint(ItemStack stack);

  /**
   * Gets the 3D box region represented by this location.
   *
   * @param stack The item stack.
   * @return The {@link IBox} region.
   */
  IBox getBox(ItemStack stack);

  /**
   * Gets the zone represented by this location.
   *
   * @param stack The item stack.
   * @return The {@link IZone}.
   */
  IZone getZone(ItemStack stack);

  /**
   * Gets the path (list of points) represented by this location.
   *
   * @param stack The item stack.
   * @return A list of {@link BlockIndex} points.
   */
  List<BlockIndex> getPath(ItemStack stack);

  /**
   * Gets the side of the point associated with this location.
   *
   * @param stack The item stack.
   * @return The {@link Direction}.
   */
  Direction getPointSide(ItemStack stack);

  /**
   * Represents the type of map location.
   */
  enum MapLocationType {
    CLEAN, SPOT, AREA, PATH, ZONE
  }
}
