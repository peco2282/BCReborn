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

import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.common.nbt.NbtReader;
import com.peco2282.bcreborn.common.nbt.NbtWriter;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;

public class MapRegion implements INBTSerializable {
  private final Int2ObjectOpenHashMap<MapChunk> chunks = new Int2ObjectOpenHashMap<>();
  private final int x, z;

  public MapRegion(int x, int z) {
    this.x = x;
    this.z = z;
  }

  public int getX() {
    return x;
  }

  public int getZ() {
    return z;
  }

  public boolean hasChunk(int x, int z) {
    return chunks.containsKey((z << 4) | x);
  }

  public MapChunk getChunk(int x, int z) {
    int id = (z << 4) | x;
    MapChunk chunk = chunks.get(id);
    if (chunk == null) {
      chunk = new MapChunk(x, z);
      chunks.put(id, chunk);
    }
    return chunk;
  }

  @Override
  public void readTag(CompoundTag tag) {
    chunks.clear();

    if (tag != null) {
      NbtReader reader = NbtReader.of(tag);
      for (int i = 0; i < 256; i++) {
        String key = "r" + i;
        if (reader.contains(key)) {
          chunks.put(i, new MapChunk(reader.getCompound(key)));
        }
      }
    }
  }

  @Override
  public void writeTag(CompoundTag tag) {
    NbtWriter writer = NbtWriter.of(tag);
    for (int i = 0; i < 256; i++) {
      MapChunk chunk = chunks.get(i);
      if (chunk != null) {
        synchronized (chunk) {
          writer.putSerializable("r" + i, chunk);
        }
      }
    }
  }
}
