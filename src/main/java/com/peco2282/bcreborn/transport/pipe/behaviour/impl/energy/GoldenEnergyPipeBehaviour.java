package com.peco2282.bcreborn.transport.pipe.behaviour.impl.energy;

import com.peco2282.bcreborn.transport.pipe.PipeMaterial;

/**
 * 金の伝導パイプの振る舞い。
 * エネルギーパイプラインの道中・末端に使用する。
 * 最大 2560RF/t まで対応。
 */
public class GoldenEnergyPipeBehaviour extends StandardEnergyPipeBehaviour {

  public static final GoldenEnergyPipeBehaviour INSTANCE = new GoldenEnergyPipeBehaviour();

  private GoldenEnergyPipeBehaviour() {
  }

  @Override
  protected int getMaxTransferRate(PipeMaterial material) {
    return PipeMaterial.GOLD.getEnergyTransferRate(); // 2560 RF/t
  }
}
