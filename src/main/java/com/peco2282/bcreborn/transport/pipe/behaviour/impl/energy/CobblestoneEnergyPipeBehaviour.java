package com.peco2282.bcreborn.transport.pipe.behaviour.impl.energy;

import com.peco2282.bcreborn.transport.pipe.PipeMaterial;

/**
 * 丸石の伝導パイプの振る舞い。
 * エネルギーパイプラインの道中・末端に使用する。
 * 最大 80RF/t まで対応。
 */
public class CobblestoneEnergyPipeBehaviour extends StandardEnergyPipeBehaviour {

  public static final CobblestoneEnergyPipeBehaviour INSTANCE = new CobblestoneEnergyPipeBehaviour();

  private CobblestoneEnergyPipeBehaviour() {
  }

  @Override
  protected int getMaxTransferRate(PipeMaterial material) {
    return PipeMaterial.COBBLESTONE.getEnergyTransferRate(); // 80 RF/t
  }
}
