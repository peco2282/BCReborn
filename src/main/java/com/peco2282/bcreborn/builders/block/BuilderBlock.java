package com.peco2282.bcreborn.builders.block;

import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class BuilderBlock extends BuildCraftBlock {
  public BuilderBlock() {
    super(Properties.of().noOcclusion().lightLevel(state -> 1));
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {

  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return null;
  }
}
