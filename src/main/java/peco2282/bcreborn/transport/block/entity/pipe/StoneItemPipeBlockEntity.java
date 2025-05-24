/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;

public class StoneItemPipeBlockEntity extends ItemPipeBlockEntity {
  public StoneItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.STONE_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.STONE);
  }

  @Contract(pure = true)
  public static void tick(
      Level world, BlockPos pos, BlockState state, @NotNull StoneItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  @Override
  protected void update(Level level, BlockPos pos, BlockState state) {}
}
