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
package com.peco2282.bcreborn.api.power;

/**
 * This interface should be defined by any Tile which wants
 * to receive energy from BuildCraft lasers.
 * <p>
 * The respective Block MUST implement ILaserTargetBlock!
 */
public interface ILaserTarget {

  /**
   * Returns true if the target currently needs power. For example, if the Advanced
   * Crafting Table has work to do.
   *
   * @return true if needs power
   */
  boolean requiresLaserEnergy();

  /**
   * Transfers energy from the laser to the target.
   *
   * @param energy
   */
  void receiveLaserEnergy(int energy);

  /**
   * Return true if the Tile Entity object is no longer a valid target. For
   * example, if its been invalidated.
   *
   * @return true if no longer a valid target object
   */
  boolean isInvalidTarget();

  /**
   * Get the X coordinate of the laser stream.
   *
   * @return
   */
  double getXCoord();

  /**
   * Get the Y coordinate of the laser stream.
   *
   * @return
   */
  double getYCoord();

  /**
   * Get the Z coordinate of the laser stream.
   *
   * @return
   */
  double getZCoord();
}
