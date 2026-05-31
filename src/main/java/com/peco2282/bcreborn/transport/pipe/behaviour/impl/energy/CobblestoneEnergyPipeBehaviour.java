/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
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
