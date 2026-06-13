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
package com.peco2282.bcreborn.api.transport;

import net.minecraft.core.Direction;

/**
 * Interface that allows blocks to override their connection behavior with pipes.
 */
public interface IPipeConnection {
  /**
   * Determines if a pipe should connect to this block.
   *
   * @param type The type of the pipe.
   * @param with The direction of the connection.
   * @return A {@link ConnectOverride} value.
   */
  ConnectOverride overridePipeConnection(IPipeTile.PipeType type, Direction with);

  /**
   * Represents connection override options.
   */
  enum ConnectOverride {
    /**
     * Force connection.
     */
    CONNECT,
    /**
     * Force disconnection.
     */
    DISCONNECT,
    /**
     * Use default connection logic.
     */
    DEFAULT
  }
}
