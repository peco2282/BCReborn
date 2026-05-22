package com.peco2282.bcreborn.builders.block;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class QuarryBlock extends BuildCraftBlock {
   public QuarryBlock() {
     super(Properties.of().noOcclusion().lightLevel(state -> 1));
   }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, BlockEntityTypesBuilders.QUARRY.get(), BuildCraftBlockEntity.ticker());
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {

  }

  @Override
  public boolean isRotatable() {
    return false;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new QuarryBlockEntity(pos, state);
  }
}
