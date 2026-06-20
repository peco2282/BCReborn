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
package com.peco2282.bcreborn.api.blocks;

/**
 * Interface for blocks that can be rotated with a Wrench.
 * <p>
 * Implementing this interface allows blocks to specify whether they support rotation
 * functionality using a Wrench tool. Only blocks that return {@code true} from
 * {@link #isRotatable()} can be rotated with a Wrench.
 * </p>
 */
public interface IRotatable {

  /**
   * Determines if this block can be rotated using a Wrench.
   * <p>
   * When this method returns {@code true}, the block can be rotated by using a Wrench tool.
   * When it returns {@code false}, Wrench rotation is disabled for this block.
   * </p>
   *
   * @return {@code true} if the block can be rotated with a Wrench, {@code false} otherwise
   */
  boolean isRotatable();

  /**
   * Determines if this block can be rotated horizontally using a Wrench.
   * <p>
   * When this method returns {@code true}, the block can only be rotated on the horizontal plane
   * (around the Y-axis) using a Wrench tool. This restricts rotation to the four horizontal
   * directions (NORTH, SOUTH, EAST, WEST) and prevents vertical orientations (UP, DOWN).
   * When it returns {@code false}, horizontal-only rotation is disabled for this block.
   * </p>
   *
   * @return {@code true} if the block can be rotated horizontally with a Wrench, {@code false} otherwise
   */
  boolean isHorizontalRotatable();
}
