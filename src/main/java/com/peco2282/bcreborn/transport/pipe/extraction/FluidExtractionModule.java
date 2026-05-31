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
package com.peco2282.bcreborn.transport.pipe.extraction;

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

/**
 * 木/エメラルド流体パイプ用の流体抽出モジュール。
 * エンジン不要で、パイプのマテリアルに応じた量を抽出する。
 * 抽出クールダウンは20tick固定。
 */
public class FluidExtractionModule implements ExtractionModule {

  public static final FluidExtractionModule INSTANCE = new FluidExtractionModule();

  private static final int EXTRACT_COOLDOWN = 20;

  private FluidExtractionModule() {
  }

  @Override
  public void extract(PipeBlockEntity pipe) {
    if (pipe.getTicksSincePull() < EXTRACT_COOLDOWN) return;

    Direction extractDir = pipe.getExtractionSide();
    BlockPos inventoryPos = pipe.getBlockPos().relative(extractDir);
    BlockEntity invBe = pipe.getLevel().getBlockEntity(inventoryPos);

    if (invBe == null || invBe instanceof PipeBlockEntity) return;

    int extractAmount = pipe.getPipeMaterial().getFluidTransferRate();

    LazyOptional<IFluidHandler> cap = invBe.getCapability(ForgeCapabilities.FLUID_HANDLER, extractDir.getOpposite());
    if (!cap.isPresent()) return;

    IFluidHandler handler = cap.orElseThrow(IllegalStateException::new);

    // タンクに既に流体がある場合は同種のみ抽出
    FluidStack existing = pipe.getFluidTank() != null ? pipe.getFluidTank().getFluid() : FluidStack.EMPTY;
    FluidStack drained;
    if (!existing.isEmpty()) {
      drained = handler.drain(new FluidStack(existing, extractAmount), IFluidHandler.FluidAction.EXECUTE);
    } else {
      drained = handler.drain(extractAmount, IFluidHandler.FluidAction.EXECUTE);
    }

    if (!drained.isEmpty() && pipe.getFluidTank() != null) {
      pipe.getFluidTank().fill(drained, IFluidHandler.FluidAction.EXECUTE);
      pipe.resetTicksSincePull();
    }
  }
}
