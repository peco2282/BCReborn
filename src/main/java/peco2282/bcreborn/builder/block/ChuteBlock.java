/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.builder.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.builder.block.entity.BCBuilderBlockEntityTypes;
import peco2282.bcreborn.builder.block.entity.ChuteBlockEtity;
import peco2282.bcreborn.lib.block.BCBaseEntityBlock;
import peco2282.bcreborn.utils.PropertyBuilder;

public class ChuteBlock extends BCBaseEntityBlock {
  public ChuteBlock(Properties properties, @NotNull String id) {
    super(properties, id, PropertyBuilder.builder().add(BCProperties.BLOCK_FACING, Direction.DOWN));
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ChuteBlockEtity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<ChuteBlock> codec() {
    return codecInstance(ChuteBlock::new);
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BCProperties.BLOCK_FACING);
  }

  @Override
  protected boolean isPathfindable(BlockState p_60475_, PathComputationType p_60478_) {
    return false;
  }

  @Override
  protected void onRemove(
      BlockState p_60515_,
      Level p_60516_,
      BlockPos p_60517_,
      BlockState p_60518_,
      boolean p_60519_) {
    Containers.dropContentsOnDestroy(p_60515_, p_60518_, p_60516_, p_60517_);
    super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
  }

  @Override
  protected <E extends BlockEntity> BlockEntityTicker<E> serverTicker(BlockEntityType<E> type) {
    return createTickerHelper(type, BCBuilderBlockEntityTypes.CHUTE.get(), ChuteBlockEtity::tick);
  }

  @Override
  protected void entityInside(
      BlockState p_60495_, Level p_60496_, BlockPos p_60497_, Entity p_60498_) {
    BlockEntity entity = p_60496_.getBlockEntity(p_60497_);
    if (entity instanceof ChuteBlockEtity hopper) {
      hopper.inside(p_60495_, p_60496_, p_60497_, p_60498_);
    }
  }
}
