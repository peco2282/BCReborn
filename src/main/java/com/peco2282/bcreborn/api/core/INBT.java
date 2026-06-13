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

/**
 * An interface for objects that can be serialized to and from NBT {@link CompoundTag}.
 * Extends Forge's {@link INBTSerializable} for compatibility.
 */
public interface INBT extends INBTSerializable<CompoundTag> {

  /**
   * Reads the object's state from the given NBT tag.
   *
   * @param nbt The NBT tag to read from.
   */
  void readTag(CompoundTag nbt);

  /**
   * Writes the object's state to the given NBT tag.
   *
   * @param nbt The NBT tag to write to.
   */
  void writeTag(CompoundTag nbt);

  @Override
  default CompoundTag serializeNBT() {
    CompoundTag nbt = new CompoundTag();
    writeTag(nbt);
    return nbt;
  }

  @Override
  default void deserializeNBT(CompoundTag nbt) {
    readTag(nbt);
  }
}
