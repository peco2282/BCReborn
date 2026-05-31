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
import com.peco2282.bcreborn.transport.pipe.behaviour.FluidPipeBehaviour;
import com.peco2282.bcreborn.transport.pipe.extraction.FluidExtractionModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 木製/エメラルド流体パイプの振る舞い（抽出ロジック）
 */
public class WoodenFluidPipeBehaviour implements FluidPipeBehaviour {

  public static final WoodenFluidPipeBehaviour INSTANCE = new WoodenFluidPipeBehaviour();

  private WoodenFluidPipeBehaviour() {
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) return;
    FluidExtractionModule.INSTANCE.extract(pipe);
  }
}
