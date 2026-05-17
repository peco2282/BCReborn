package com.peco2282.bcreborn.transport.pipe.behaviour.impl.energy;

import com.peco2282.bcreborn.transport.pipe.PipeMaterial;

/**
 * 焼石の伝導パイプの振る舞い。
 * エネルギーパイプラインの道中・末端に使用する。
 * 最大 160RF/t まで対応。
 */
public class StoneEnergyPipeBehaviour extends StandardEnergyPipeBehaviour {

  public static final StoneEnergyPipeBehaviour INSTANCE = new StoneEnergyPipeBehaviour();

  private StoneEnergyPipeBehaviour() {
  }

  @Override
  protected int getMaxTransferRate(PipeMaterial material) {
    return PipeMaterial.STONE.getEnergyTransferRate(); // 160 RF/t
  }
}
