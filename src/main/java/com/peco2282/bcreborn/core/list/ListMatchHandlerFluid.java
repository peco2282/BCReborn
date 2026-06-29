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
package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.common.utils.FluidUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class ListMatchHandlerFluid extends ListMatchHandler {
  @Override
  public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
    if (type == Type.TYPE) {
      if (FluidUtils.isFluidContainer(stack) && FluidUtils.isFluidContainer(target)) {
        // We don't have a direct equivalent of drainFluidContainer easily without modifying the stack,
        // but we can check if they are the same type of container.
        return stack.getItem() == target.getItem();
      }
    } else if (type == Type.MATERIAL) {
      FluidStack fStack = FluidUtils.getFluidStackFromItemStack(stack);
      FluidStack fTarget = FluidUtils.getFluidStackFromItemStack(target);
      if (!fStack.isEmpty() && !fTarget.isEmpty()) {
        return fStack.isFluidEqual(fTarget);
      }
    }
    return false;
  }

  @Override
  public boolean isValidSource(Type type, ItemStack stack) {
    if (type == Type.TYPE) {
      return FluidUtils.isFluidContainer(stack);
    } else if (type == Type.MATERIAL) {
      return !FluidUtils.getFluidStackFromItemStack(stack).isEmpty();
    }
    return false;
  }

  @Override
  public List<ItemStack> getClientExamples(Type type, ItemStack stack) {
    // Example generation for fluids is complex in 1.20.1 due to the lack of a central registry of all filled containers.
    // We will return the stack itself for now.
    if (type == Type.MATERIAL || type == Type.TYPE) {
      if (isValidSource(type, stack)) {
        return List.of(stack);
      }
    }
    return List.of();
  }
}
