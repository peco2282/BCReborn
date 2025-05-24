/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.builder.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.lib.block.entity.BCBaseBlockEntity;

public class QuarryBlockEntity extends BCBaseBlockEntity {
  public QuarryBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCBuilderBlockEntityTypes.QUARRY.get(), p_155229_, p_155230_);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, QuarryBlockEntity entity) {}
}
