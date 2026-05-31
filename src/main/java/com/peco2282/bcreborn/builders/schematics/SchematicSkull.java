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
package com.peco2282.bcreborn.builders.schematics;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;

public class SchematicSkull extends SchematicTile {

  @Override
  public void rotateLeft(IBuilderContext context) {
    int rot = tileNBT.getByte("Rot");

    rot = (rot + 4) % 16;

    tileNBT.putByte("Rot", (byte) rot);
  }

}
