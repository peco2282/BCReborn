/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public interface Facing {
  default void addFacingProperty(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(getFacingProperty());
  }

  default Property<Direction> getFacingProperty() {
    return BCProperties.BLOCK_FACING;
  }
}
