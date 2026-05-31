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


import com.peco2282.bcreborn.common.builder.schematics.SchematicRotateMeta;

public class SchematicBuilderLike extends SchematicRotateMeta {
  public SchematicBuilderLike() {
  }

  @Override
  public void onNBTLoaded() {
    if (tileNBT != null) {
      tileNBT.remove("box");
      tileNBT.remove("bpt");
      tileNBT.remove("bptBuilder");
      tileNBT.remove("builderState");
      tileNBT.remove("done");
      tileNBT.remove("iterator");
      tileNBT.remove("path");
    }
  }
}
