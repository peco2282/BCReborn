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
package com.peco2282.bcreborn.common.utils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.Optional;

public final class FluidUtils {
  private FluidUtils() {
  }

  public static FluidStack getFluidStackFromBlock(BlockState state) {
    Block b = state.getBlock();
    if (b instanceof IFluidBlock fb) {
      Fluid f = fb.getFluid();
      if (f != null) {
        return new FluidStack(f, 1000);
      }
    } else if (b instanceof BucketPickup) {
      Fluid f = state.getFluidState().getType();
      if (f != Fluids.EMPTY) {
        return new FluidStack(f, 1000);
      }
    }
    return FluidStack.EMPTY;
  }

  public static FluidStack getFluidStackFromItemStack(ItemStack stack) {
    if (!stack.isEmpty()) {
      Optional<IFluidHandlerItem> cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();
      if (cap.isPresent()) {
        IFluidHandlerItem handler = cap.get();
        if (handler.getTanks() > 0) {
          return handler.getFluidInTank(0);
        }
      } else if (stack.getItem() instanceof BlockItem bi) {
        return getFluidStackFromBlock(bi.getBlock().defaultBlockState());
      }
    }
    return FluidStack.EMPTY;
  }

  public static Fluid getFluidFromItemStack(ItemStack stack) {
    FluidStack fluidStack = getFluidStackFromItemStack(stack);
    return fluidStack.getFluid();
  }

  public static boolean isFluidContainer(ItemStack stack) {
    return !stack.isEmpty() && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
  }
}
