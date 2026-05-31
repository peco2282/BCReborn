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
