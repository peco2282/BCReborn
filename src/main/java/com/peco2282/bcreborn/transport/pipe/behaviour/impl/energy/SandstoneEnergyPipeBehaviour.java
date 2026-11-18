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

import com.peco2282.bcreborn.transport.block.PipeBlock;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Sandstone power pipe (original {@code PipePowerSandstone}): carries 320 RF/t and connects only to other pipes, so
 * it can run past engines and machines without touching them.
 */
public class SandstoneEnergyPipeBehaviour extends StandardEnergyPipeBehaviour {
  public static final SandstoneEnergyPipeBehaviour INSTANCE = new SandstoneEnergyPipeBehaviour();

  private SandstoneEnergyPipeBehaviour() {
  }

  @Override
  public boolean canConnectTo(PipeBlockEntity pipe, Direction dir, BlockState neighbor) {
    return neighbor.getBlock() instanceof PipeBlock && super.canConnectTo(pipe, dir, neighbor);
  }
}
