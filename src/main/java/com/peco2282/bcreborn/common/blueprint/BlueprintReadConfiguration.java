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
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.core.ISerializable;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class BlueprintReadConfiguration implements ISerializable {
  public boolean rotate = true;
  public boolean excavate = true;
  public boolean allowCreative = false;

  public void writeToNBT(CompoundTag nbttagcompound) {
    nbttagcompound.putBoolean("rotate", rotate);
    nbttagcompound.putBoolean("excavate", excavate);
    nbttagcompound.putBoolean("allowCreative", allowCreative);
  }

  public void readFromNBT(CompoundTag nbttagcompound) {
    rotate = nbttagcompound.getBoolean("rotate");
    excavate = nbttagcompound.getBoolean("excavate");
    allowCreative = nbttagcompound.getBoolean("allowCreative");
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    int flags = stream.readUnsignedByte();
    rotate = (flags & 1) != 0;
    excavate = (flags & 2) != 0;
    allowCreative = (flags & 4) != 0;
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeByte(
        (rotate ? 1 : 0) |
            (excavate ? 2 : 0) |
            (allowCreative ? 4 : 0)
    );
  }
}
