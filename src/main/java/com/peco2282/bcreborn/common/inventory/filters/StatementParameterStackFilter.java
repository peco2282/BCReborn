/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.inventory.filters;

import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

/**
 * Returns true if the stack matches any one one of the filter stacks.
 */
public class StatementParameterStackFilter extends ArrayStackOrListFilter {

	public StatementParameterStackFilter(IStatementParameter... parameters) {
		ArrayList<ItemStack> tmp = new ArrayList<ItemStack>();

		for (IStatementParameter s : parameters) {
			if (s != null) {
				if (s instanceof StatementParameterItemStack) {
					tmp.add(s.getItemStack());
				}
			}
		}

		stacks = tmp.toArray(new ItemStack[tmp.size()]);
	}
}
