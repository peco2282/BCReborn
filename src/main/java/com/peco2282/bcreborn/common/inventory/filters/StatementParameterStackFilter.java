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
