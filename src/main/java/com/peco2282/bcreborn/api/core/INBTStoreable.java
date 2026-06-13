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

/**
 * Interface for objects that can be saved to and loaded from NBT.
 */
public interface INBTStoreable {
  /**
   * Reads the object's state from the given NBT tag.
   *
   * @param tag The NBT tag to read from.
   */
  void readFromNBT(CompoundTag tag);

  /**
   * Writes the object's state to the given NBT tag.
   *
   * @param tag The NBT tag to write to.
   */
  void writeToNBT(CompoundTag tag);
}
