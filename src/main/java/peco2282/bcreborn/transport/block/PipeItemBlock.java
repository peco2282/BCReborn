/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.transport.block.entity.pipe.ItemPipeBlockEntity;
import peco2282.bcreborn.transport.block.pipe.PipeMaterialHandler;
import peco2282.bcreborn.utils.PropertyBuilder;

public class PipeItemBlock extends BCBasePipeBlock {
  private final PipeMaterialHandler HANDLER = new PipeMaterialHandler(getPipeMaterial());
  private @Nullable ItemPipeBlockEntity entity = null;

  public PipeItemBlock(Properties properties, PipeMaterial material, PropertyBuilder builder) {
    super(properties, material, PipeType.ITEM, builder);
  }

  // for Codec
  private PipeItemBlock(Properties properties, PipeMaterial material, PipeType type) {
    super(properties, material, type, PropertyBuilder.builder());
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return entity = HANDLER.newBlockEntity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<PipeItemBlock> codec() {
    return codecInstance(PipeItemBlock::new);
  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(
      BlockEntityType<E> type) {
    return HANDLER.serverTicker(type, PipeItemBlock::createTickerHelper);
  }

  @Override
  protected void neighborChanged(
      BlockState p_60509_,
      Level p_60510_,
      BlockPos p_60511_,
      Block p_60512_,
      BlockPos p_60513_,
      boolean p_60514_) {
    super.neighborChanged(p_60509_, p_60510_, p_60511_, p_60512_, p_60513_, p_60514_);
    entity = p_60510_.getBlockEntity(p_60511_) instanceof ItemPipeBlockEntity pipe ? pipe : null;
  }
}
