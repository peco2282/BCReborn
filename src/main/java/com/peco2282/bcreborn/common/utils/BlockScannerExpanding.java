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

import java.util.Iterator;

public class BlockScannerExpanding implements Iterable<BlockPos> {

  private int searchRadius;
  private int searchX;
  private int searchY;
  private int searchZ;

  public BlockScannerExpanding() {
    searchRadius = 1;
    searchX = -1;
    searchY = -1;
    searchZ = -1;
  }

  @Override
  public Iterator<BlockPos> iterator() {
    return new BlockIt();
  }

  public BlockPos nextBlockPos() {
    return new BlockIt().next();
  }

  class BlockIt implements Iterator<BlockPos> {

    @Override
    public boolean hasNext() {
      return searchRadius < 64;
    }

    @Override
    public BlockPos next() {
      // Step through each block in a hollow cube of size (searchRadius * 2 -1), if done
      // add 1 to the radius and start over.

      BlockPos next = new BlockPos(searchX, searchY, searchZ);

      // Step to the next Y
      if (Math.abs(searchX) == searchRadius || Math.abs(searchZ) == searchRadius) {
        searchY += 1;
      } else {
        searchY += searchRadius * 2;
      }

      if (searchY > searchRadius) {
        // Step to the next Z
        searchY = -searchRadius;
        searchZ += 1;

        if (searchZ > searchRadius) {
          // Step to the next X
          searchZ = -searchRadius;
          searchX += 1;

          if (searchX > searchRadius) {
            // Step to the next radius
            searchRadius += 1;
            searchX = -searchRadius;
            searchY = -searchRadius;
            searchZ = -searchRadius;
          }
        }
      }
      return next;
    }

    @Override
    public void remove() {
    }
  }
}
