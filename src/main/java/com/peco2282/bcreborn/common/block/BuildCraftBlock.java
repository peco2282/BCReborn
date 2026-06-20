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
package com.peco2282.bcreborn.common.block;

import com.peco2282.bcreborn.api.blocks.IRotatable;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class BuildCraftBlock extends BaseEntityBlock implements IRotatable {
  public BuildCraftBlock(Properties p_49795_) {
    super(p_49795_);
  }

  public abstract void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_);

  @Override
  public RenderShape getRenderShape(BlockState p_49232_) {
    return RenderShape.MODEL;
  }

  @Override
  public boolean isRotatable() {
    return true;
  }
}
