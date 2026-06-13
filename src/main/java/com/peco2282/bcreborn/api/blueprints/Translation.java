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
package com.peco2282.bcreborn.api.blueprints;

import com.peco2282.bcreborn.api.core.Position;

/**
 * Represents a translation transform in 3D space.
 */
public class Translation {

  public double x = 0;
  public double y = 0;
  public double z = 0;

  /**
   * Translates the given position by the values in this transform.
   *
   * @param p The position to translate.
   * @return A new {@link Position} representing the translated coordinates.
   */
  public Position translate(Position p) {
    Position p2 = new Position(p);

    p2.x = p.x + x;
    p2.y = p.y + y;
    p2.z = p.z + z;

    return p2;
  }

  @Override
  public String toString() {
    return "{" + x + ", " + y + ", " + z + "}";
  }

}
