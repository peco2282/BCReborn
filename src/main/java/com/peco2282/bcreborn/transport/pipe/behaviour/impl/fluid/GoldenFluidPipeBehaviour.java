package com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 金液体パイプの振る舞い
 * 通過中のみ高速（80mB/tick）。接続制限なし。
 */
public class GoldenFluidPipeBehaviour extends StandardFluidPipeBehaviour {

  public static final GoldenFluidPipeBehaviour INSTANCE = new GoldenFluidPipeBehaviour();

  private GoldenFluidPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    return true;
  }
}
