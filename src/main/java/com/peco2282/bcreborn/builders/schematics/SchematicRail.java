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
import com.peco2282.bcreborn.common.builder.schematics.SchematicBlockFloored;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;

public class SchematicRail extends SchematicBlockFloored {
  @Override
  public void rotateLeft(IBuilderContext context) {
    if (state != null) {
      state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
    }
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    super.placeInWorld(context, x, y, z, stacks);
  }

  @Override
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    BlockState worldState = context.world().getBlockState(new BlockPos(x, y, z));
    if (state != null) {
      return worldState.getBlock() == state.getBlock();
    }
    return worldState.getBlock() == block;
  }

  @Override
  public void postProcessing(IBuilderContext context, int x, int y, int z) {
    if (state != null) {
      context.world().setBlock(new BlockPos(x, y, z), state, 3);
    }
  }
}
