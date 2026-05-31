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
package com.peco2282.bcreborn.api.fuels;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;

import java.util.Collection;

public interface IFuelManager {
    IFuel addFuel(IFuel fuel);

    IFuel addFuel(Fluid fluid, int powerPerCycle, int totalBurningTime);

    IFuel addFuel(FluidType fluid, int powerPerCycle, int totalBurningTime);

    Collection<IFuel> getFuels();

    IFuel getFuel(FluidType fluid);

    IFuel getFuel(Fluid fluid);
}
