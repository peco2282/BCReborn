package com.peco2282.bcreborn.transport.pipe.behaviour.impl.energy;

import com.peco2282.bcreborn.transport.pipe.PipeMaterial;

/**
 * 水晶の伝導パイプの振る舞い。
 * エネルギーパイプラインの道中・末端に使用する。
 * 最大 640RF/t まで対応。
 */
public class QuartzEnergyPipeBehaviour extends StandardEnergyPipeBehaviour {

  public static final QuartzEnergyPipeBehaviour INSTANCE = new QuartzEnergyPipeBehaviour();

  private QuartzEnergyPipeBehaviour() {
  }

  @Override
  protected int getMaxTransferRate(PipeMaterial material) {
    return PipeMaterial.QUARTZ.getEnergyTransferRate(); // 640 RF/t
  }
}
