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

import java.util.Random;

public interface IZone {

  double distanceTo(BlockIndex index);

  double distanceToSquared(BlockIndex index);

  boolean contains(double x, double y, double z);

  default boolean contains(BlockPos pos) {
    return contains(pos.getX(), pos.getY(), pos.getZ());
  }

  BlockIndex getRandomBlockIndex(Random rand);

}
