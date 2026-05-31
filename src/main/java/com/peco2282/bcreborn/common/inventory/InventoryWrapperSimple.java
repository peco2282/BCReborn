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
package com.peco2282.bcreborn.common.inventory;

import com.peco2282.bcreborn.common.utils.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InventoryWrapperSimple extends InventoryWrapper {

	private final int[] slots;

	public InventoryWrapperSimple(Container inventory) {
		super(inventory);
		slots = Utils.createSlotArray(0, inventory.getContainerSize());
	}

	@Override
	public int[] getSlotsForFace(Direction var1) {
		return slots;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slotIndex, ItemStack itemstack, @Nullable Direction side) {
		return canPlaceItem(slotIndex, itemstack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack itemstack, Direction side) {
		return true;
	}

}
