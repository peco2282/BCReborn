package peco2282.bcreborn.api.mj.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.mj.MJConnector;

public class MJEngineConnector implements MJConnector {
  @Override
  public boolean isActive(Level level, BlockPos pos, BlockState state) {
    return state.getValue(BCProperties.ACTIVE);
  }

  @Override
  public boolean canConnection() {
    return true;
  }
}
