package peco2282.bcreborn.api.mj.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.mj.MJReceiver;

public record MJReceiverSimpleImpl(long require) implements MJReceiver {
  @Override
  public <B extends Block & BCBlock> B receivedBy() {
    return null;
  }

  @Override
  public boolean isActive(Level level, BlockPos pos, BlockState state) {
    return true;
  }
}
