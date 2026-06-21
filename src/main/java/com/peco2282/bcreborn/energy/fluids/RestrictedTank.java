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

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class RestrictedTank extends Tank {

	private final Fluid[] acceptedFluids;

	public RestrictedTank(String name, int capacity, Fluid... acceptedFluids) {
		super(name, capacity);
		this.acceptedFluids = acceptedFluids;
	}

  @Override
  public int fill(FluidStack resource, FluidAction action) {
		if (!acceptsFluid(resource.getFluid())) {
			return 0;
		} else {
			return super.fill(resource, action);
		}
	}

	public boolean acceptsFluid(Fluid fluid) {
		for (Fluid accepted : acceptedFluids) {
			if (accepted.equals(fluid)) {
				return true;
			}
		}

		return false;
	}
}
