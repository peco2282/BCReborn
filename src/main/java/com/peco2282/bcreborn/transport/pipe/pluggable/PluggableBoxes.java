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
package com.peco2282.bcreborn.transport.pipe.pluggable;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

/**
 * Places a box that is described relative to a pipe side (original {@code MatrixTranformations.transform()}).
 */
public final class PluggableBoxes {

  private PluggableBoxes() {
  }

  /**
   * @param side      the pipe side the box sits on
   * @param min       start of the box on the two axes across the side
   * @param max       end of the box on the two axes across the side
   * @param nearDepth distance of the near face from the block face on {@code side}
   * @param farDepth  distance of the far face from the block face on {@code side}
   */
  public static AABB box(Direction side, float min, float max, float nearDepth, float farDepth) {
    boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
    double from = positive ? 1.0 - farDepth : nearDepth;
    double to = positive ? 1.0 - nearDepth : farDepth;
    return switch (side.getAxis()) {
      case X -> new AABB(from, min, min, to, max, max);
      case Y -> new AABB(min, from, min, max, to, max);
      case Z -> new AABB(min, min, from, max, max, to);
    };
  }
}
