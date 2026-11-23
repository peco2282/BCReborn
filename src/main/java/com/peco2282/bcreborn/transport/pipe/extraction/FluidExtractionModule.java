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

import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraft.world.item.ItemStack;
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
/**
 * Original {@code PipeFluidsWood.updateEntity()} / {@code extractFluid()} (and {@code PipeFluidsEmerald}): every
 * tick, a pipe with fluid to pull ({@link FluidExtractionBattery}, filled by engines) drains up to its flow rate
 * from the block on its extraction side and puts it into itself through that side, so the side is an input and the
 * pipe does not push the fluid back. The buffer goes down by the flow rate each tick whether or not anything came.
 * Without energy nothing is pulled (it used to pull for free every 20 ticks).
 */
public class FluidExtractionModule implements ExtractionModule {

  public static final FluidExtractionModule INSTANCE = new FluidExtractionModule();

  private FluidExtractionModule() {
  }

  @Override
  public void extract(PipeBlockEntity pipe) {
    extract(pipe, null);
  }

  /**
   * @param filter the fluid to pull (an Emerald pipe's filter), or null for any
   */
  public void extract(PipeBlockEntity pipe, @Nullable FluidStack filter) {
    if (!(pipe.getBattery() instanceof FluidExtractionBattery battery) || battery.getLiquid() <= 0) return;

    Direction extractDir = pipe.getExtractionSide();
    BlockEntity source = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(extractDir));
    IFluidHandler handler = source == null || source instanceof PipeBlockEntity ? null
      : source.getCapability(ForgeCapabilities.FLUID_HANDLER, extractDir.getOpposite()).orElse(null);
    if (handler == null) {
      // Original: nothing to pull from, the buffer is dropped.
      battery.setEnergy(0);
      return;
    }

    int flowRate = pipe.getPipeMaterial().getFluidTransferRate();
    int amount = Math.min(battery.getLiquid(), flowRate);
    IFluidHandler into = pipe.getCapability(ForgeCapabilities.FLUID_HANDLER, extractDir).orElse(null);
    if (into != null) {
      FluidStack existing = pipe.getFluidTank() != null ? pipe.getFluidTank().getFluid() : FluidStack.EMPTY;
      FluidStack wanted = filter != null ? new FluidStack(filter, amount)
        : !existing.isEmpty() ? new FluidStack(existing, amount) : null;
      FluidStack extracted = wanted != null ? handler.drain(wanted, IFluidHandler.FluidAction.SIMULATE)
        : handler.drain(amount, IFluidHandler.FluidAction.SIMULATE);
      if (!extracted.isEmpty()) {
        int inserted = into.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
        if (inserted > 0) {
          handler.drain(new FluidStack(extracted, inserted), IFluidHandler.FluidAction.EXECUTE);
        }
      }
    }
    battery.spend(flowRate);
    pipe.setChanged();
  }

  /** Original PipeFluidsEmerald: the fluid in the filter item (a bucket, a tank item...), if any. */
  @Nullable
  public static FluidStack filterFluid(ItemStack filter) {
    if (filter.isEmpty()) return null;
    Optional<FluidStack> fluid = FluidUtil.getFluidContained(filter);
    return fluid.filter(stack -> !stack.isEmpty()).orElse(null);
  }
}
