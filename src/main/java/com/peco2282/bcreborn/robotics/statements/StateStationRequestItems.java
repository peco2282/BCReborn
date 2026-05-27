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
