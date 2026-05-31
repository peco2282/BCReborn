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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * ボイド流体パイプの振る舞い（流体消滅）
 */
public class VoidFluidPipeBehaviour implements FluidPipeBehaviour {

  public static final VoidFluidPipeBehaviour INSTANCE = new VoidFluidPipeBehaviour();

  private VoidFluidPipeBehaviour() {
  }

  @Override
  public void tick(PipeBlockEntity pipe, Level level, BlockPos pos, BlockState state) {
    var tank = pipe.getFluidTank();
    if (tank != null && tank.getFluidAmount() > 0) {
      tank.drain(tank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
    }
  }

  // Void パイプは流体を外部へ転送しない（全て消滅させる）
  @Override
  public boolean canOutputFluid(PipeBlockEntity pipe, Direction dir) {
    return false;
  }
}
