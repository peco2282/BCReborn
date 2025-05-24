/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.lib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCBlockEntity;

/**
 * Represents an abstract container block entity specific to the Neptune system. This class extends
 * {@link BaseContainerBlockEntity} and implements {@link BCBlockEntity}.
 *
 * @author peco2282
 */
public abstract class BCBaseContainerBlockEntity extends BaseContainerBlockEntity
    implements BCBlockEntity {
  /**
   * Constructs a new BCBaseContainerBlockEntity.
   *
   * @param type The type of the block entity.
   * @param pos The position of the block in the world.
   * @param state The current state of the block.
   */
  protected BCBaseContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }
}
