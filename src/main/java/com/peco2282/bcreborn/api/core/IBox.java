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

/**
 * Interface representing a 3D box region in the world.
 * Extends {@link IZone}.
 */
public interface IBox extends IZone {

  /**
   * Expands the box in all directions by the specified amount.
   *
   * @param amount The amount to expand.
   * @return A new {@link IBox} representing the expanded region.
   */
  IBox expand(int amount);

  /**
   * Contracts the box in all directions by the specified amount.
   *
   * @param amount The amount to contract.
   * @return A new {@link IBox} representing the contracted region.
   */
  IBox contract(int amount);

  /**
   * Gets the minimum coordinates of the box.
   *
   * @return The minimum position.
   */
  Position pMin();

  /**
   * Gets the maximum coordinates of the box.
   *
   * @return The maximum position.
   */
  Position pMax();

  /**
   * Creates data for rendering lasers to outline the box.
   */
  void createLaserData();

}
