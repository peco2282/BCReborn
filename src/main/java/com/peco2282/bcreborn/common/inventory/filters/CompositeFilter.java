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

/**
 * Returns true if the stack matches any one one of the filter stacks.
 */
public class CompositeFilter implements IStackFilter {

	private final IStackFilter[] filters;

	public CompositeFilter(IStackFilter... iFilters) {
		filters = iFilters;
	}

	@Override
	public boolean matches(ItemStack stack) {
		for (IStackFilter f : filters) {
			if (f.matches(stack)) {
				return true;
			}
		}

		return false;
	}
}
