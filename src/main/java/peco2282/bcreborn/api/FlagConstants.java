package peco2282.bcreborn.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @since 1.0
 * @see Level#setBlock(BlockPos, BlockState, int)
 *
 */
@SuppressWarnings("PointlessBitwiseExpression")
public interface FlagConstants {
  int BLOCK_UPDATE = 1 << 0;
  int SEND_TO_CLIENT = 1 << 1;
  int NO_RENDERER = 1 << 2;
  int RERENDER_ON_MAIN_THREAD = 1 << 3;
  int NO_NEIGHBOR_SHAPE_UPDATE = 1 << 4;
  int NO_OBSERVERS = 1 << 5;
  int FORCE_RERENDER = 1 << 6;
}
