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
package com.peco2282.bcreborn.api.tiles;

/**
 * This interface should be implemented by any Tile Entity which wishes to
 * have non-redstone automation (for example, BuildCraft Gates, but also
 * other mods which implement it, e.g. OpenComputers).
 */
public interface IControllable {
  /**
   * Get the current control mode of the Tile Entity.
   *
   * @return The current control mode.
   */
  Mode getControlMode();

  /**
   * Set the mode of the Tile Entity.
   *
   * @param mode The new control mode.
   */
  void setControlMode(Mode mode);

  /**
   * Check if a given control mode is accepted.
   * If you query IControllable tiles, you MUST check with
   * acceptsControlMode first.
   *
   * @param mode The control mode to check.
   * @return True if this control mode is accepted.
   */
  boolean acceptsControlMode(Mode mode);

  enum Mode {
    Unknown, On, Off, Mode, Loop
  }
}
