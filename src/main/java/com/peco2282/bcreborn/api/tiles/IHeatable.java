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
package com.peco2282.bcreborn.api.tiles;

/**
 * This interface should be implemented by Tile Entities
 * which have an internal heat value.
 */
public interface IHeatable {
  /**
   * @return The minimum heat value, in degrees.
   */
  double getMinHeatValue();

  /**
   * @return The preferred heat value, in degrees.
   */
  double getIdealHeatValue();

  /**
   * @return The maxmimum heat value, in degrees.
   */
  double getMaxHeatValue();

  /**
   * @return The current heat value, in degrees.
   */
  double getCurrentHeatValue();

  /**
   * Set the heat of the tile.
   *
   * @param value Heat value, in degrees.
   * @return The heat the tile has after the set.
   */
  double setHeatValue(double value);
}
