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

