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

import com.peco2282.bcreborn.api.gates.IGate;
import net.minecraft.core.Direction;

/**
 * Interface representing a pipe.
 */
public interface IPipe {
  /**
   * Gets the {@link IPipeBlockEntity} associated with this pipe.
   *
   * @return The pipe tile.
   */
  IPipeBlockEntity getBlockEntity();

  /**
   * Gets the gate on the specified side of the pipe.
   *
   * @param side The side to check.
   * @return The {@link IGate}, or null if there is no gate.
   */
  IGate getGate(Direction side);

  /**
   * Checks if there is a gate on the specified side of the pipe.
   *
   * @param side The side to check.
   * @return True if a gate is present.
   */
  boolean hasGate(Direction side);

  /**
   * Checks if the pipe is wired with the specified {@link PipeWire}.
   *
   * @param wire The wire color.
   * @return True if the wire is present.
   */
  boolean isWired(PipeWire wire);

  /**
   * Checks if the specified {@link PipeWire} is currently active (signal is ON).
   *
   * @param wire The wire color.
   * @return True if the wire is active.
   */
  boolean isWireActive(PipeWire wire);
}
