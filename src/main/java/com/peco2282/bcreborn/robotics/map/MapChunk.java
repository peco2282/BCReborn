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
package com.peco2282.bcreborn.robotics.map;

import com.peco2282.bcreborn.api.core.BCLog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

public class MapChunk {
  private static final int VERSION = 1;

  private int x, z;
  private byte[] data;

  public MapChunk(int x, int z) {
    this.x = x;
    this.z = z;
    data = new byte[256];
  }

  public MapChunk(CompoundTag compound) {
    readFromNBT(compound);
  }

  public int getX() {
    return x;
  }

  public int getZ() {
    return z;
  }

  public int getColor(int x, int z) {
    return data[((z & 15) << 4) | (x & 15)];
  }

  public void update(ChunkAccess chunk) {
    for (int bz = 0; bz < 16; bz++) {
      for (int bx = 0; bx < 16; bx++) {
        int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, bx, bz);
        int color = MapColor.NONE.id;

        if (y < 0) {
          y = 255;
        }

        BlockState b;

        while (y >= chunk.getMinBuildHeight()) {
          BlockPos pos = new BlockPos(bx, y, bz);
          b = chunk.getBlockState(pos);
          color = b.getMapColor(chunk, pos).id;
          if (color != MapColor.NONE.id) {
            break;
          }
          y--;
        }

        data[(bz << 4) | bx] = (byte) color;
      }
    }
  }

  public void readFromNBT(CompoundTag compound) {
    int version = compound.getShort("version");
    if (version > MapChunk.VERSION) {
      BCLog.logger.error("Unsupported MapChunk version: " + version);
      return;
    }
    x = compound.getInt("x");
    z = compound.getInt("z");
    data = compound.getByteArray("data");
    if (data.length != 256) {
      BCLog.logger.error("Invalid MapChunk data length: " + data.length);
      data = new byte[256];
    }
  }

  public void writeToNBT(CompoundTag compound) {
    compound.putShort("version", (short) VERSION);
    compound.putInt("x", x);
    compound.putInt("z", z);
    compound.putByteArray("data", data);
  }

  @Override
  public int hashCode() {
    return 31 * x + z;
  }
}
