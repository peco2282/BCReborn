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
package com.peco2282.bcreborn.api.boards;

/**
 * Represents a redstone board that can be updated and persisted using NBT data.
 * <p>
 * This interface defines the core functionality for redstone boards, which are components
 * that manage redstone-related logic and state within a container. The board can be updated
 * with the current state of its container and provides an NBT handler for data persistence.
 *
 * @param <T> The type of container that this redstone board operates within.
 */
public interface IRedstoneBoard<T> {

  /**
   * Updates the board's state based on the current state of the container.
   * <p>
   * This method is typically called periodically (e.g., each tick) to allow the board
   * to process redstone logic, update its internal state, and interact with the container.
   *
   * @param container The container instance that this board is operating within.
   */
  void updateBoard(T container);

  /**
   * Retrieves the NBT handler responsible for saving and loading this board's data.
   * <p>
   * The NBT handler manages the serialization and deserialization of the board's state,
   * allowing it to be persisted across game sessions.
   *
   * @return The {@link RedstoneBoardNBT} handler for this board.
   */
  RedstoneBoardNBT<?> getNBTHandler();
}
