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
package com.peco2282.bcreborn.core.properties;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class DimensionProperty {

  private final Long2ObjectMap<ChunkProperty> chunkMapping = new Long2ObjectOpenHashMap<>();
  private final Level world;
  private final int worldHeight;
  private final int minBuildHeight;
  private final WorldProperty worldProperty;

  public DimensionProperty(Level iWorld, WorldProperty iProp) {
    world = iWorld;
    worldHeight = iWorld.getHeight();
    minBuildHeight = iWorld.getMinBuildHeight();
    worldProperty = iProp;
  }

  public synchronized boolean get(int x, int y, int z) {
    int xChunk = x >> 4;
    int zChunk = z >> 4;

    if (world.hasChunk(xChunk, zChunk)) {
      long chunkId = ChunkPos.asLong(xChunk, zChunk);
      ChunkProperty property;
      if (!chunkMapping.containsKey(chunkId)) {
        property = new ChunkProperty(world, worldHeight, minBuildHeight);
        chunkMapping.put(chunkId, property);
        load(world.getChunk(xChunk, zChunk), property);
      } else {
        property = chunkMapping.get(chunkId);
      }

      return property.get(x & 0xF, y, z & 0xF);
    } else {
      return false;
    }
  }

  private void load(LevelChunk chunk, ChunkProperty property) {
    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    for (int x = 0; x < 16; ++x) {
      for (int y = minBuildHeight; y < minBuildHeight + worldHeight; ++y) {
        for (int z = 0; z < 16; ++z) {
          pos.set(chunk.getPos().getMinBlockX() + x, y, chunk.getPos().getMinBlockZ() + z);
          boolean prop = worldProperty.get(world, chunk.getBlockState(pos), pos);
          property.set(x, y, z, prop);
        }
      }
    }
  }

  public void clear() {
    chunkMapping.clear();
  }

}
