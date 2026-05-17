package com.peco2282.bcreborn.transport.pipe.behaviour.impl.fluid;

/**
 * 水晶液体パイプの振る舞い。
 * 接続制限なし。輸送速度上限: 40mB/tick
 */
public class QuartzFluidPipeBehaviour extends StandardFluidPipeBehaviour {

  public static final QuartzFluidPipeBehaviour INSTANCE = new QuartzFluidPipeBehaviour();

  private QuartzFluidPipeBehaviour() {
  }

}
