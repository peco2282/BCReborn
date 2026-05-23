/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
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
