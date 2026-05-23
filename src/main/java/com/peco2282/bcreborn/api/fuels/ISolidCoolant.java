/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.fuels;

import com.peco2282.bcreborn.api.core.StackKey;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public interface ISolidCoolant {
    StackKey getSolid();
    FluidStack getFluidFromSolidCoolant(ItemStack stack);
}
