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

import net.minecraft.network.FriendlyByteBuf;

/**
 * Implemented by classes that can be serialized to and from a {@link FriendlyByteBuf}.
 * This is typically used for network packet synchronization.
 */
public interface IBufferSerializable {

  /**
   * Serializes the object's state to the buffer.
   *
   * @param data The buffer to write to.
   */
  void writeData(FriendlyByteBuf data);

  /**
   * Deserializes the object's state from the buffer.
   *
   * @param data The buffer to read from.
   */
  void readData(FriendlyByteBuf data);
}

