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

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class MapUtils {
  private MapUtils() {

  }

  public static long getIDFromCoords(int x, int z) {
    return ((long) (x & 0xFFFFFF) << 24) | (z & 0xFFFFFF);
  }

  public static long getIDFromCoords(ChunkPos pos) {
    return getIDFromCoords(pos.x, pos.z);
  }

  public static long getIDFromCoords(ChunkAccess chunk) {
    return getIDFromCoords(chunk.getPos());
  }

  public static int getXFromID(long id) {
    int x = (int) ((id >> 24) & 0xFFFFFF);
    return x >= 0x800000 ? x - 0x1000000 : x;
  }

  public static int getZFromID(long id) {
    // Two's complement of the 24 bits (it was off by one for every negative z, so regions were saved to the
    // wrong file names).
    int z = (int) (id & 0xFFFFFF);
    return z >= 0x800000 ? z - 0x1000000 : z;
  }

}
