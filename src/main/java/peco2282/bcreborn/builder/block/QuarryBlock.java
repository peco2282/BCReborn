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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.api.block.VolumeMarked;
import peco2282.bcreborn.builder.block.entity.BCBuilderBlockEntityTypes;
import peco2282.bcreborn.builder.block.entity.QuarryBlockEntity;
import peco2282.bcreborn.lib.block.BCBaseEntityBlock;
import peco2282.bcreborn.utils.PropertyBuilder;

public class QuarryBlock extends BCBaseEntityBlock implements RotatableFacing, VolumeMarked {
  public QuarryBlock(Properties properties, @NotNull String id) {
    super(
        properties,
        id,
        PropertyBuilder.builder()
            .add(BCProperties.BLOCK_FACING, Direction.NORTH)
            .add(BCProperties.ACTIVE, false));
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new QuarryBlockEntity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<? extends BCBaseEntityBlock> codec() {
    return codecInstance(QuarryBlock::new);
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BCProperties.BLOCK_FACING).add(BCProperties.ACTIVE);
  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(
      BlockEntityType<E> type) {
    return createTickerHelper(
        type, BCBuilderBlockEntityTypes.QUARRY.get(), QuarryBlockEntity::tick);
  }

  @Override
  public boolean isAvailable(Level level, BlockPos pos, BlockState state) {
    return state.getValue(BCProperties.ACTIVE);
  }
}
