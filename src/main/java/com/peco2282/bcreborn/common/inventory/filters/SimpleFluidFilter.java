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
package com.peco2282.bcreborn.common.inventory.filters;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class SimpleFluidFilter implements IFluidFilter {

	private Fluid fluidChecked;

	public SimpleFluidFilter(FluidStack stack) {
		if (stack != null) {
			fluidChecked = stack.getFluid();
		}
	}

	@Override
	public boolean matches(Fluid fluid) {
		if (fluidChecked != null) {
			return fluidChecked == fluid;
		} else {
			return false;
		}
	}
}
