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
import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.common.nbt.NbtReader;
import com.peco2282.bcreborn.common.nbt.NbtWriter;
import com.peco2282.bcreborn.common.utils.BitSetUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;

import java.util.BitSet;

public class ZoneChunk implements IBufferSerializable, INBTSerializable {

  public BitSet property;
  private boolean fullSet = false;

  public ZoneChunk() {
  }

  public boolean get(int xChunk, int zChunk) {
    if (fullSet) {
      return true;
    } else if (property == null) {
      return false;
    } else {
      return property.get(xChunk + zChunk * 16);
    }
  }

  public void set(int xChunk, int zChunk, boolean value) {
    if (value) {
      if (fullSet) {
        return;
      }

      if (property == null) {
        property = new BitSet(16 * 16);
      }

      property.set(xChunk + zChunk * 16, value);

      if (property.cardinality() >= 16 * 16) {
        property = null;
        fullSet = true;
      }
    } else {
      if (fullSet) {
        property = new BitSet(16 * 16);
        property.flip(0, 16 * 16 - 1);
        fullSet = false;
      } else if (property == null) {
        // Note - ZonePlan should usually destroy such chunks
        property = new BitSet(16 * 16);
      }

      property.set(xChunk + zChunk * 16, value);
    }
  }

  @Override
  public void writeTag(CompoundTag nbt) {
    NbtWriter.of(nbt)
        .putBoolean("fullSet", fullSet)
        .putByteArray("bits", property == null ? new byte[0] : BitSetUtils.toByteArray(property));
  }

  @Override
  public void readTag(CompoundTag nbt) {
    NbtReader.of(nbt)
        .applyBoolean("fullSet", fullSet -> this.fullSet = fullSet)
        .applyByteArray("bits", bits -> property = BitSetUtils.fromByteArray(bits));
  }

  public BlockPos getRandomBlockIndex(RandomSource rand) {
    int x, z;

    if (fullSet) {
      x = rand.nextInt(16);
      z = rand.nextInt(16);
    } else {
      int bitId = rand.nextInt(property.cardinality());
      int bitPosition = property.nextSetBit(0);

      while (bitId > 0) {
        bitId--;

        bitPosition = property.nextSetBit(bitPosition + 1);
      }

      z = bitPosition / 16;
      x = bitPosition - 16 * z;
    }
    int y = rand.nextInt(255);

    return new BlockPos(x, y, z);
  }

  public boolean isEmpty() {
    return !fullSet && (property == null || property.isEmpty());
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    int flags = stream.readUnsignedByte();
    if ((flags & 1) != 0) {
      property = BitSetUtils.fromByteArray(stream.readByteArray());
    }
    fullSet = (flags & 2) != 0;
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    int flags = (fullSet ? 2 : 0) | (property != null ? 1 : 0);
    stream.writeByte(flags);
    if (property != null) {
      stream.writeByteArray(BitSetUtils.toByteArray(property));
    }
  }
}
