/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.slots;


import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotValidated extends Slot {

	public SlotValidated(Container inv, int id, int x, int y) {
		super(inv, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack itemStack) {
		return container.canPlaceItem(this.getSlotIndex(), itemStack);
	}
}
