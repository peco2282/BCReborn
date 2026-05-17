package com.peco2282.bcreborn.transport.pipe.behaviour;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraftforge.fluids.FluidStack;

/**
 * 流体パイプ固有の振る舞いを定義するインターフェース
 */
public interface FluidPipeBehaviour extends PipeBehaviour {
  /**
   * 流体がパイプを通過する時の処理
   */
  default void onFluidPassed(PipeBlockEntity pipe, FluidStack stack) {
  }

  /**
   * 指定方向への流体出力が許可されているか。
   * デフォルトは全方向許可。Iron Fluid Pipe などで方向制限に使用する。
   */
  default boolean canOutputFluid(PipeBlockEntity pipe, net.minecraft.core.Direction dir) {
    return true;
  }

  /**
   * 流体を転送する処理
   */
  default void transferFluid(PipeBlockEntity pipe) {
  }
}
