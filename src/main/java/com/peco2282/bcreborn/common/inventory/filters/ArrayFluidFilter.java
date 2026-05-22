/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.inventory.filters;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

/**
 * Returns true if the stack matches any one one of the filter stacks.
 */
public class ArrayFluidFilter implements IFluidFilter {

	protected Fluid[] fluids;

	public ArrayFluidFilter(ItemStack... stacks) {
		fluids = new Fluid[stacks.length];

		for (int i = 0; i < stacks.length; ++i) {
			FluidStack stack = FluidUtil.getFluidContained(stacks[i]).orElse(FluidStack.EMPTY);
			if (!stack.isEmpty()) {
				fluids[i] = stack.getFluid();
			}
		}
	}

	public ArrayFluidFilter(Fluid... iFluids) {
		fluids = iFluids;
	}


	public boolean hasFilter() {
		for (Fluid filter : fluids) {
			if (filter != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean matches(Fluid fluid) {
		for (Fluid filter : fluids) {
			if (filter != null && fluid == filter) {
				return true;
			}
		}

		return false;
	}
}
