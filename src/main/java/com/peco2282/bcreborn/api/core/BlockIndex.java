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

/**
 * A container for integer-based block positions that implements {@link Comparable}.
 */
public class BlockIndex {
  public static boolean nextTo(BlockPos index1, BlockPos index2) {
    return (Math.abs(index1.getX() - index2.getX()) <= 1 && index1.getY() == index2.getY() && index1.getZ() == index2.getZ())
            || (index1.getX() == index2.getX() && Math.abs(index1.getY() - index2.getY()) <= 1 && index1.getZ() == index2.getZ())
            || (index1.getX() == index2.getX() && index1.getY() == index2.getY() && Math.abs(index1.getZ() - index2.getZ()) <= 1);
  }
}
