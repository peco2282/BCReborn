/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.capability.mj;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Base interface for mj caps. */
public interface MJCapability {
  boolean isActive(Level level, BlockPos pos, BlockState state);

  default Direction[] validSides() {
    return Direction.values();
  }
}
