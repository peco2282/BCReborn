package peco2282.bcreborn.api.capability.mj.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.capability.mj.MJConnector;

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
