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

import com.peco2282.bcreborn.api.core.IBufferSerializable;
import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.api.serialization.NbtReader;
import com.peco2282.bcreborn.api.serialization.NbtWriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class BlueprintReadConfiguration implements IBufferSerializable, INBTSerializable {
  public boolean rotate = true;
  public boolean excavate = true;
  public boolean allowCreative = false;

  @Override
  public void writeTag(CompoundTag nbttagcompound) {
    NbtWriter.of(nbttagcompound)
      .putBoolean("rotate", rotate)
      .putBoolean("excavate", excavate)
      .putBoolean("allowCreative", allowCreative)
      .done();
  }

  @Override
  public void readTag(CompoundTag nbttagcompound) {
    NbtReader.of(nbttagcompound)
      .applyBoolean("rotate", it -> rotate = it)
      .applyBoolean("excavate", it -> excavate = it)
      .applyBoolean("allowCreative", it -> allowCreative = it)
      .done();
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
