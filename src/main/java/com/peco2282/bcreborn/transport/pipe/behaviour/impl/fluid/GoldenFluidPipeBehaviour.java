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
