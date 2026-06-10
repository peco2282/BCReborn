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

import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class SchematicRotatableBlock extends SchematicBlock {
  public SchematicRotatableBlock(BlockState state) {
    this.state = state;
  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    this.state = this.state.rotate(context.world(), null, Rotation.COUNTERCLOCKWISE_90);
  }
}
