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
package com.peco2282.bcreborn.transport.block.entity;

import com.peco2282.bcreborn.transport.pipe.transport.FluidTransportModule;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

/**
 * 流体パイプの IFluidHandler ラッパー。
 * fill() 時に FluidTransportModule.onFluidFilled() を呼び出して逆流防止の TransferState を設定する。
 * また、異種流体の混入を防ぐ。
 */
public class PipeFluidHandler implements IFluidHandler {

  private final PipeBlockEntity pipe;
  private final FluidTank tank;
  private Direction fillDirection = null;

  public PipeFluidHandler(PipeBlockEntity pipe, FluidTank tank) {
    this.pipe = pipe;
    this.tank = tank;
  }

  /**
   * fill() を呼ぶ前に方向を設定する。
   * Capability の side 情報を利用するため、PipeBlockEntity.getCapability() で設定する。
   */
  public void setFillDirection(Direction dir) {
    this.fillDirection = dir;
  }

  @Override
  public int getTanks() {
    return tank.getTanks();
  }

  @Override
  public @NotNull FluidStack getFluidInTank(int tank) {
    return this.tank.getFluidInTank(tank);
  }

  @Override
  public int getTankCapacity(int tank) {
    return this.tank.getTankCapacity(tank);
  }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
    return this.tank.isFluidValid(tank, stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    // 異種流体混入防止: タンクに既に別の流体がある場合は拒否
    FluidStack existing = tank.getFluid();
    if (!existing.isEmpty() && !existing.isFluidEqual(resource)) {
      return 0;
    }

    int filled = tank.fill(resource, action);

    if (filled > 0 && action == FluidAction.EXECUTE) {
      // 流入方向を Input 状態にして逆流を防ぐ
      FluidTransportModule module = pipe.getFluidTransportModule();
      if (module != null && fillDirection != null) {
        module.onFluidFilled(fillDirection);
      }
      pipe.setChanged();
    }

    return filled;
  }

  @Override
  public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
    return tank.drain(resource, action);
  }

  @Override
  public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
    return tank.drain(maxDrain, action);
  }
}
