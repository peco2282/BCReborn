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

import java.util.List;

/**
 * To be implemented by TileEntities able to provide a path on the world, typically BuildCraft path markers.
 */
public interface IPathProvider {
  /**
   * Gets the list of block positions that define the path.
   *
   * @return A list of {@link BlockIndex}.
   */
  List<BlockIndex> getPath();

  /**
   * Remove from the world all objects used to define the path.
   */
  void removeFromWorld();
}
