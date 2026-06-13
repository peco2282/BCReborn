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
 * To be implemented by BlockEntities able to provide a 3D area in the world, typically BuildCraft markers.
 */
public interface IAreaProvider {

  /**
   * @return The minimum x-coordinate of the area.
   */
  int xMin();

  /**
   * @return The minimum y-coordinate of the area.
   */
  int yMin();

  /**
   * @return The minimum z-coordinate of the area.
   */
  int zMin();

  /**
   * @return The maximum x-coordinate of the area.
   */
  int xMax();

  /**
   * @return The maximum y-coordinate of the area.
   */
  int yMax();

  /**
   * @return The maximum z-coordinate of the area.
   */
  int zMax();

  /**
   * Removes from the world all objects used to define the area (e.g., markers).
   */
  void removeFromWorld();
}
