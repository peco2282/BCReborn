package com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid;

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 丸石液体パイプの振る舞い
 * 石の液体パイプとは接続しない。輸送速度上限: 10mB/tick
 */
public class CobblestoneFluidPipeBehaviour extends StandardFluidPipeBehaviour {

  public static final CobblestoneFluidPipeBehaviour INSTANCE = new CobblestoneFluidPipeBehaviour();

  private CobblestoneFluidPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      return otherPipe.getPipeMaterial() != PipeMaterial.STONE;
    }
    return true;
  }
}
