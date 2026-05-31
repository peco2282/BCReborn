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
package com.peco2282.bcreborn.api.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface INBT extends INBTSerializable<CompoundTag> {
  void read(CompoundTag nbt);

  void write(CompoundTag nbt);

  @Override
  default CompoundTag serializeNBT() {
    CompoundTag nbt = new CompoundTag();
    write(nbt);
    return nbt;
  }

  @Override
  default void deserializeNBT(CompoundTag nbt) {
    read(nbt);
  }
}
