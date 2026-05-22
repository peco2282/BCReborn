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
import net.minecraftforge.common.ForgeHooks;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 */
public enum StackFilter implements IStackFilter {

	ALL {
		@Override
		public boolean matches(ItemStack stack) {
			return true;
		}
	},
	FUEL {
		@Override
		public boolean matches(ItemStack stack) {
			return ForgeHooks.getBurnTime(stack, null) > 0;
		}
	};

	@Override
	public abstract boolean matches(ItemStack stack);
}
