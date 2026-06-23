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

import com.peco2282.bcreborn.api.core.IZone;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.Iterator;

public class BlockScannerZoneRandom implements Iterable<BlockPos> {

  private final RandomSource rand;
  private final IZone zone;
  private final int x;
  private final int y;
  private final int z;

  public BlockScannerZoneRandom(int iX, int iY, int iZ, RandomSource iRand, IZone iZone) {
    x = iX;
    y = iY;
    z = iZ;
    rand = iRand;
    zone = iZone;
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
      BlockPos block = zone.getRandomBlockIndex(rand);
      return new BlockPos(block.getX() - x, block.getY() - y, block.getZ() - z);
    }

    @Override
    public void remove() {
    }
  }

}
