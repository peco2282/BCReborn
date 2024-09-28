package peco2282.bcreborn.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IEngine {
  boolean isActive(Level level, BlockPos pos, BlockState state);
}
