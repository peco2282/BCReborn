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
package com.peco2282.bcreborn.common;

import com.peco2282.bcreborn.api.core.IBufferSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;


public class ChunkIndex implements IBufferSerializable {
  public int x, z;

  public ChunkIndex() {

  }

  public ChunkIndex(int iX, int iZ) {
    x = iX;
    z = iZ;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ChunkIndex c) {

      return c.x == x && c.z == z;
    }

    return super.equals(obj);
  }


  @Override
  public int hashCode() {
    return x * 37 + z;
  }

  public void writeToNBT(CompoundTag nbt) {
    nbt.putInt("x", x);
    nbt.putInt("z", z);
  }

  public void readFromNBT(CompoundTag nbt) {
    x = nbt.getInt("x");
    z = nbt.getInt("z");
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    x = stream.readInt();
    z = stream.readInt();
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeInt(x);
    stream.writeInt(z);
  }
}
