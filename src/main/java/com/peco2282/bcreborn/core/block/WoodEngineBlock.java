package com.peco2282.bcreborn.core.block;

import com.peco2282.bcreborn.common.block.EngineBlock;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import com.peco2282.bcreborn.core.block.entity.WoodEngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class WoodEngineBlock extends EngineBlock {
  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
    return BlockEntityTypesCore.WOODEN_ENGINE.get().create(p_153215_, p_153216_);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
    return createTickerHelper(p_153214_, BlockEntityTypesCore.WOODEN_ENGINE.get(), WoodEngineBlockEntity.ticker());
  }
}
