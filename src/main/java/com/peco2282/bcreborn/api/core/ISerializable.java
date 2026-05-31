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
 * Implemented by classes representing serializable packet state
 */
public interface ISerializable {
  /**
   * Serializes the state to the stream
   *
   * @param data
   */
  void writeData(FriendlyByteBuf data);

  /**
   * Deserializes the state from the stream
   *
   * @param data
   */
  void readData(FriendlyByteBuf data);
}

