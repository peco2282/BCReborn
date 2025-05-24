/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.Stream;

public interface VolumeMarked extends MarkedObject {
  default void blockAction(Level level, Stream<BlockPos> stream) {
    stream.forEach(p -> action(level.getBlockState(p)));
  }

  default void action(BlockState state) {}
}
