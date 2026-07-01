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


import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.nbt.NbtReader;
import com.peco2282.bcreborn.common.nbt.NbtWriter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class BlockScanner implements Iterable<BlockPos>, INBTSerializable {

  Box box = new Box();
  Level world;

  int x, y, z;
  int iterationsPerCycle;
  int blocksDone = 0;

  public BlockScanner(Box box, Level world, int iterationsPreCycle) {
    this.box = box;
    this.world = world;
    this.iterationsPerCycle = iterationsPreCycle;

    x = box.xMin;
    y = box.yMin;
    z = box.zMin;
  }

  public BlockScanner() {
  }

  @Override
  public Iterator<BlockPos> iterator() {
    return new BlockIt();
  }

  public int totalBlocks() {
    return box.sizeX() * box.sizeY() * box.sizeZ();
  }

  public int blocksLeft() {
    return totalBlocks() - blocksDone;
  }

  @Override
  public void writeTag(CompoundTag nbt) {
    NbtWriter.of(nbt)
      .putInt("x", x)
      .putInt("y", y)
      .putInt("z", z)
      .putInt("blocksDone", blocksDone)
      .putInt("iterationsPerCycle", iterationsPerCycle)
      .putSerializable("box", box)
      .done();
  }

  @Override
  public void readTag(CompoundTag nbt) {
    NbtReader.of(nbt)
      .applyInt("x", it -> x = it)
      .applyInt("y", it -> y = it)
      .applyInt("z", it -> z = it)
      .applyInt("blocksDone", it -> blocksDone = it)
      .applyInt("iterationsPerCycle", it -> iterationsPerCycle = it)
      .applyCompound("box", it -> box.initialize(it))
      .done();
  }

  public BlockPos nextBlockPos() {
    return iterator().next();
  }

  class BlockIt implements Iterator<BlockPos> {

    int it = 0;

    @Override
    public boolean hasNext() {
      return z <= box.zMax && it <= iterationsPerCycle;
    }

    @Override
    public BlockPos next() {
      BlockPos index = new BlockPos(x, y, z);
      it++;
      blocksDone++;

      if (x < box.xMax) {
        x++;
      } else {
        x = box.xMin;

        if (y < box.yMax) {
          y++;
        } else {
          y = box.yMin;

          z++;
        }
      }

      return index;
    }

    @Override
    public void remove() {

    }
  }

}
