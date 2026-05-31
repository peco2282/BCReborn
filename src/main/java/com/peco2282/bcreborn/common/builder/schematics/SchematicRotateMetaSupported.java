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
package com.peco2282.bcreborn.common.builder.schematics;

import com.google.common.collect.Sets;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.core.BlockIndex;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Set;

public class SchematicRotateMetaSupported extends SchematicRotateMeta {

  public SchematicRotateMetaSupported() {
    super();
  }

  @Override
  public Set<BlockIndex> getPrerequisiteBlocks(IBuilderContext context) {
    Direction side = Direction.NORTH;
    if (state != null) {
      if (state.hasProperty(BlockStateProperties.FACING)) {
        side = state.getValue(BlockStateProperties.FACING);
      } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
        side = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
      }
    }
    return Sets.newHashSet(RELATIVE_INDEXES[side.get3DDataValue()]);
  }
}
