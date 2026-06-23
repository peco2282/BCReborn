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
package com.peco2282.bcreborn.common.utils;


import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.Iterator;

public class BlockScannerRandom implements Iterable<BlockPos> {

  private final RandomSource rand;
  private final int maxDistance;

  public BlockScannerRandom(RandomSource iRand, int iMaxDistance) {
    this.rand = iRand;
    this.maxDistance = iMaxDistance;
  }

  @Override
  public Iterator<BlockPos> iterator() {
    return new BlockIt();
  }

  class BlockIt implements Iterator<BlockPos> {

    @Override
    public boolean hasNext() {
      return true;
    }

    @Override
    public BlockPos next() {
      double radius = rand.nextFloat() * maxDistance;
      float polarAngle = rand.nextFloat() * 2.0F * (float) Math.PI;
      float azimuthAngle = rand.nextFloat() * (float) Math.PI;

      int searchX = (int) (radius * Mth.cos(polarAngle) * Mth.sin(azimuthAngle));
      int searchY = (int) (radius * Mth.cos(azimuthAngle));
      int searchZ = (int) (radius * Mth.sin(polarAngle) * Mth.sin(azimuthAngle));

      return new BlockPos(searchX, searchY, searchZ);
    }

    @Override
    public void remove() {
    }
  }

}
