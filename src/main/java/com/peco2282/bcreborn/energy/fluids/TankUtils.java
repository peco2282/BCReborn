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
package com.peco2282.bcreborn.energy.fluids;

import com.peco2282.bcreborn.common.block.BlockEntityBuffer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

public final class TankUtils {

  /**
   * Deactivate constructor
   */
  private TankUtils() {
  }

  public static boolean handleRightClick(IFluidHandler tank, Direction side, Player player, boolean fill, boolean drain) {
    ItemStack current = player.getItemInHand(InteractionHand.MAIN_HAND);
    if (!current.isEmpty()) {
      if (fill && FluidUtil.getFluidContained(current).isPresent()) {
        return FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, tank);
      } else if (drain) {
        return FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, tank);
      }
    }
    return false;
  }

  public static Block getFluidBlock(Fluid fluid, boolean moving) {
    if (fluid == Fluids.WATER) {
      return moving ? Blocks.WATER_CAULDRON : Blocks.WATER;
    }
    if (fluid == Fluids.LAVA) {
      return moving ? Blocks.LAVA_CAULDRON : Blocks.LAVA;
    }
    return fluid.defaultFluidState().createLegacyBlock().getBlock();
  }

  public static void pushFluidToConsumers(IFluidTank tank, int flowCap, BlockEntityBuffer[] tileBuffer) {
    for (Direction side : Direction.values()) {
      FluidStack fluidStack = tank.drain(flowCap, IFluidHandler.FluidAction.SIMULATE);
      if (!fluidStack.isEmpty() && fluidStack.getAmount() > 0) {
        BlockEntity tile = tileBuffer[side.ordinal()].getTile();
        if (tile != null) {
          tile.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).ifPresent(handler -> {
            int used = handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
            if (used > 0) {
              tank.drain(used, IFluidHandler.FluidAction.EXECUTE);
            }
          });
        }
      }
    }
  }
}
