package com.peco2282.bcreborn.energy.block;

import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.energy.BlockEntityTypesEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeEngineBlock extends EngineBlock {
  @Override
  public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return BlockEntityTypesEnergy.CREATIVE_ENGINE.get().create(p_153215_, p_153216_);
  }
}
