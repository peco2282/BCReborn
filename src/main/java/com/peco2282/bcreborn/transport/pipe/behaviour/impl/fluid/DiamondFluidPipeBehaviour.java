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
import com.peco2282.bcreborn.transport.pipe.behaviour.FluidPipeBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * ダイヤモンド液体パイプの挙動。
 * フィルターに一致する液体を対応する方向へ優先的に転送する。
 * 転送速度は金の液体パイプと同じ（80mB/tick）。
 */
public class DiamondFluidPipeBehaviour implements FluidPipeBehaviour {

  public static final DiamondFluidPipeBehaviour INSTANCE = new DiamondFluidPipeBehaviour();

  private DiamondFluidPipeBehaviour() {
  }


  @Override
  public void transferFluid(PipeBlockEntity pipe) {
    var fluidTank = pipe.getFluidTank();
    if (fluidTank == null || fluidTank.getFluidAmount() <= 0) return;

    int maxTransfer = pipe.getPipeMaterial().getFluidTransferRate();
    FluidStack current = fluidTank.getFluid();

    // フィルターに一致する方向を優先
    List<Direction> filteredTargets = new ArrayList<>();
    List<Direction> emptyFilterTargets = new ArrayList<>();

    for (Direction dir : Direction.values()) {
      if (!pipe.getBlockState().getValue(PipeBlock.PROPERTY_MAP.get(dir))) continue;
      BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be == null) continue;
      if (!be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).isPresent()) continue;

      var filter = pipe.getFilter(dir);
      boolean hasMatch = false;
      boolean filterEmpty = true;
      for (int i = 0; i < filter.getContainerSize(); i++) {
        ItemStack filterStack = filter.getItem(i);
        if (!filterStack.isEmpty()) {
          filterEmpty = false;
          // FluidUtilでフィルタースタックから液体を取得し、現在の液体と比較
          var contained = net.minecraftforge.fluids.FluidUtil.getFluidContained(filterStack);
          if (contained.isPresent() && contained.get().getFluid() == current.getFluid()) {
            hasMatch = true;
          }
        }
      }
      if (hasMatch) {
        filteredTargets.add(dir);
      } else if (filterEmpty) {
        emptyFilterTargets.add(dir);
      }
    }

    List<Direction> targets = filteredTargets.isEmpty() ? emptyFilterTargets : filteredTargets;
    if (targets.isEmpty()) return;

    int available = Math.min(fluidTank.getFluidAmount(), maxTransfer);
    int toTransfer = available / targets.size();
    if (toTransfer <= 0) toTransfer = 1;

    for (Direction dir : targets) {
      BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be == null) continue;
      final int amount = toTransfer;
      be.getCapability(ForgeCapabilities.FLUID_HANDLER, dir.getOpposite()).ifPresent(handler -> {
        FluidStack drained = fluidTank.drain(amount, IFluidHandler.FluidAction.SIMULATE);
        int filled = handler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
      });
    }
  }
}
