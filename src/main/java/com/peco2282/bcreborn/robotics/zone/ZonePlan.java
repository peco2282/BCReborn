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
package com.peco2282.bcreborn.robotics.zone;

import com.peco2282.bcreborn.api.core.IBufferSerializable;
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.common.ChunkIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;


public class ZonePlan implements IZone, IBufferSerializable {
  private final HashMap<ChunkIndex, ZoneChunk> chunkMapping = new HashMap<>();

  public boolean get(int x, int z) {
    int xChunk = x >> 4;
    int zChunk = z >> 4;
    ChunkIndex chunkId = new ChunkIndex(xChunk, zChunk);
    ZoneChunk property;

    if (!chunkMapping.containsKey(chunkId)) {
      return false;
    } else {
      property = chunkMapping.get(chunkId);
      return property.get(x & 0xF, z & 0xF);
    }
  }

  public void set(int x, int z, boolean val) {
    int xChunk = x >> 4;
    int zChunk = z >> 4;
    ChunkIndex chunkId = new ChunkIndex(xChunk, zChunk);
    ZoneChunk property;

    if (!chunkMapping.containsKey(chunkId)) {
      if (val) {
        property = new ZoneChunk();
        chunkMapping.put(chunkId, property);
      } else {
        return;
      }
    } else {
      property = chunkMapping.get(chunkId);
    }

    property.set(x & 0xF, z & 0xF, val);

    if (property.isEmpty()) {
      chunkMapping.remove(chunkId);
    }
  }

  public void writeToNBT(CompoundTag nbt) {
    ListTag list = new ListTag();

    for (Map.Entry<ChunkIndex, ZoneChunk> e : chunkMapping.entrySet()) {
      CompoundTag subNBT = new CompoundTag();
      e.getKey().writeToNBT(subNBT);
      e.getValue().writeToNBT(subNBT);
      list.add(subNBT);
    }

    nbt.put("chunkMapping", list);
  }

  public void readFromNBT(CompoundTag nbt) {
    ListTag list = nbt.getList("chunkMapping", ListTag.TAG_COMPOUND);

    for (int i = 0; i < list.size(); ++i) {
      CompoundTag subNBT = list.getCompound(i);

      ChunkIndex id = new ChunkIndex();
      id.readFromNBT(subNBT);

      ZoneChunk chunk = new ZoneChunk();
      chunk.readFromNBT(subNBT);

      chunkMapping.put(id, chunk);
    }
  }

  @Override
  public double distanceTo(BlockPos index) {
    return Math.sqrt(distanceToSquared(index));
  }

  @Override
  public double distanceToSquared(BlockPos index) {
    double maxSqrDistance = Double.MAX_VALUE;

    for (Map.Entry<ChunkIndex, ZoneChunk> e : chunkMapping.entrySet()) {
      double dx = (e.getKey().x << 4 + 8) - index.getX();
      double dz = (e.getKey().z << 4 + 8) - index.getZ();

      double sqrDistance = dx * dx + dz * dz;

      if (sqrDistance < maxSqrDistance) {
        maxSqrDistance = sqrDistance;
      }
    }

    return maxSqrDistance;
  }

  @Override
  public boolean contains(double x, double y, double z) {
    int xBlock = (int) Math.floor(x);
    int zBlock = (int) Math.floor(z);

    return get(xBlock, zBlock);
  }

  @Override
  public BlockPos getRandomBlockIndex(RandomSource rand) {
    if (chunkMapping.isEmpty()) {
      return null;
    }

    int chunkId = rand.nextInt(chunkMapping.size());

    for (Map.Entry<ChunkIndex, ZoneChunk> e : chunkMapping.entrySet()) {
      if (chunkId == 0) {
        BlockPos i = e.getValue().getRandomBlockIndex(rand);
        i = i.offset(e.getKey().x << 4, 0, e.getKey().z << 4);

        return i;
      }

      chunkId--;
    }

    return BlockPos.ZERO;
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    chunkMapping.clear();
    int size = stream.readInt();
    for (int i = 0; i < size; i++) {
      ChunkIndex key = new ChunkIndex();
      ZoneChunk value = new ZoneChunk();
      key.readData(stream);
      value.readData(stream);
      chunkMapping.put(key, value);
    }
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeInt(chunkMapping.size());
    for (Map.Entry<ChunkIndex, ZoneChunk> e : chunkMapping.entrySet()) {
      e.getKey().writeData(stream);
      e.getValue().writeData(stream);
    }
  }
}
