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
package com.peco2282.bcreborn.robotics.statements;

import java.util.LinkedList;

import com.peco2282.bcreborn.api.statements.ActionState;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import net.minecraft.world.item.ItemStack;

public class StateStationRequestItems extends ActionState {

	LinkedList<ItemStack> items;

	public StateStationRequestItems(LinkedList<ItemStack> filter) {
		items = filter;
	}

	public boolean matches(IStackFilter filter) {
		if (items.size() == 0) {
			return true;
		} else {
			for (ItemStack stack : items) {
				if (filter.matches(stack)) {
					return true;
				}
			}
		}

		return false;
	}

}
