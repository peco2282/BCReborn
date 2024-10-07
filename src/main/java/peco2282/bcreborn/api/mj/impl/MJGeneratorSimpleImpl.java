package peco2282.bcreborn.api.mj.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.mj.MJGenerator;

public class MJGeneratorSimpleImpl implements MJGenerator {
  private final long per;

  public MJGeneratorSimpleImpl(long per) {
    this.per = per;
  }

  @Override
  public long perTick(Level level, BlockState state) {
    return per;
  }

  @Override
  public boolean isActive(Level level, BlockPos pos, BlockState state) {
    return true;
  }
}
