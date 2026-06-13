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
package com.peco2282.bcreborn.api;


/**
 * This interface should be implemented by any BlockEntity which wishes to
 * support non-redstone automation (for example, BuildCraft Gates or integration from other mods).
 */
public interface IControllable {

  /**
   * Gets the current control mode of the BlockEntity.
   *
   * @return The current {@link Mode}.
   */
  Mode getControlMode();

  /**
   * Sets the control mode of the BlockEntity.
   *
   * @param mode The new {@link Mode} to set.
   */
  void setControlMode(Mode mode);

  /**
   * Checks if a given control mode is supported by this BlockEntity.
   * When interacting with {@link IControllable} objects, you should check this before calling {@link #setControlMode(Mode)}.
   *
   * @param mode The {@link Mode} to check.
   * @return True if the mode is accepted.
   */
  boolean acceptsControlMode(Mode mode);

  /**
   * Represents various control modes for automation.
   */
  enum Mode {
    Unknown, On, Off, Mode, Loop
  }
}
