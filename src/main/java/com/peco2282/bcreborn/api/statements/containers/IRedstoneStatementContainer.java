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
package com.peco2282.bcreborn.api.statements.containers;

import net.minecraft.core.Direction;

public interface IRedstoneStatementContainer {
  /**
   * Get the redstone input from a given side.
   *
   * @param side The side - use "UNKNOWN" for maximum input.
   * @return The redstone input, from 0 to 15.
   */
  int getRedstoneInput(Direction side);

  /**
   * Set the redstone input for a given side.
   *
   * @param side The side - use "UNKNOWN" for all sides.
   * @return Whether the set was successful.
   */
  boolean setRedstoneOutput(Direction side, int value);
}
