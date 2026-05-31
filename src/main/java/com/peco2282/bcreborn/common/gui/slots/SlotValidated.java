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
