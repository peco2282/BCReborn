package peco2282.bcreborn.api.mj;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base interface for mj caps.
 */
public interface MJCapability {
  boolean isActive(Level level, BlockPos pos, BlockState state);

  default Direction[] validSides() {
    return Direction.values();
  }
}
