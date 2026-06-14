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
package com.peco2282.bcreborn.api.core;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.Random;

/**
 * Interface representing a 3D zone in the world.
 */
public interface IZone {

  /**
   * Calculates the distance to a given block index.
   *
   * @param index The block index.
   * @return The distance.
   */
  double distanceTo(BlockIndex index);

  /**
   * Calculates the squared distance to a given block index.
   *
   * @param index The block index.
   * @return The squared distance.
   */
  double distanceToSquared(BlockIndex index);

  /**
   * Checks if the zone contains the specified coordinates.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   * @param z The z-coordinate.
   * @return True if the zone contains the point.
   */
  boolean contains(double x, double y, double z);

  /**
   * Checks if the zone contains the specified block position.
   *
   * @param pos The block position.
   * @return True if the zone contains the point.
   */
  default boolean contains(BlockPos pos) {
    return contains(pos.getX(), pos.getY(), pos.getZ());
  }

  /**
   * Gets a random block index within the zone.
   *
   * @param rand The random instance to use.
   * @return A random {@link BlockIndex}.
   */
  BlockIndex getRandomBlockIndex(RandomSource rand);

}
