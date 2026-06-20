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
package com.peco2282.bcreborn.factory.block;

import com.peco2282.bcreborn.common.block.BuildCraftBlock;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import com.peco2282.bcreborn.factory.block.entity.MiningWellBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

public class MiningWellBlock extends BuildCraftBlock {
  public MiningWellBlock() {
    super(Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(5.0F, 10.0F));
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new MiningWellBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, FactoryBlockEntityTypes.MINING_WELL.get(), BuildCraftBlockEntity.ticker());
  }

  @Override
  public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      removePipes(level, pos);
    }
    super.onRemove(state, level, pos, newState, isMoving);
  }

  private void removePipes(Level level, BlockPos pos) {
    for (int depth = pos.getY() - 1; depth > level.getMinBuildHeight(); depth--) {
      BlockPos pipePos = new BlockPos(pos.getX(), depth, pos.getZ());
      BlockState pipeState = level.getBlockState(pipePos);
      if (!(pipeState.getBlock() instanceof PlainPipeBlock)) {
        break;
      }
      level.removeBlock(pipePos, false);
    }
  }

  @Override
  public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
  }
}
