/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.inventory;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidHandlerCopy implements IFluidHandler {

	private IFluidHandler orignal;
	private FluidStack[] contents;
	private int[] capacities;

	public FluidHandlerCopy(IFluidHandler orignal) {
		this.orignal = orignal;

		int tanks = orignal.getTanks();
		contents = new FluidStack[tanks];
		capacities = new int[tanks];

		for (int i = 0; i < tanks; i++) {
			contents[i] = orignal.getFluidInTank(i).copy();
			capacities[i] = orignal.getTankCapacity(i);
		}
	}

	@Override
	public int getTanks() {
		return contents.length;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return contents[tank];
	}

	@Override
	public int getTankCapacity(int tank) {
		return capacities[tank];
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return orignal.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}
