package com.peco2282.bcreborn.common.block;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public abstract class BuildCraftBlock extends BaseEntityBlock implements IRotatable {
  public BuildCraftBlock(Properties p_49795_) {
    super(p_49795_);
  }

  public abstract void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_);
}
