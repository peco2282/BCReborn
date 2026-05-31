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

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 砂岩液体パイプの振る舞い
 * 砂岩パイプ同士のみ接続可能。輸送速度上限: 20mB/tick
 */
public class SandstoneFluidPipeBehaviour extends StandardFluidPipeBehaviour {

  public static final SandstoneFluidPipeBehaviour INSTANCE = new SandstoneFluidPipeBehaviour();

  private SandstoneFluidPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    if (neighbor.getBlock() instanceof PipeBlock otherPipe) {
      return otherPipe.getPipeMaterial() == PipeMaterial.SANDSTONE;
    }
    return true;
  }
}
