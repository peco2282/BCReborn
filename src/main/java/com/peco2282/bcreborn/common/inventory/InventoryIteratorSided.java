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

import com.peco2282.bcreborn.api.core.IInvSlot;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

import java.util.Iterator;

class InventoryIteratorSided implements Iterable<IInvSlot> {

	private final WorldlyContainer inv;
	private final Direction side;

	InventoryIteratorSided(WorldlyContainer inv, Direction side) {
		this.inv = inv;
		this.side = side;
	}

	@Override
	public Iterator<IInvSlot> iterator() {
		return new Iterator<>() {
            final int[] slots = inv.getSlotsForFace(side);
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < slots.length;
            }

            @Override
            public IInvSlot next() {
                return new InvSlot(slots[index++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }

        };
	}

	private class InvSlot implements IInvSlot {

		private int slot;

		public InvSlot(int slot) {
			this.slot = slot;
		}

		@Override
		public ItemStack getStackInSlot() {
			return inv.getItem(slot);
		}

		@Override
		public void setStackInSlot(ItemStack stack) {
			inv.setItem(slot, stack);
		}

		@Override
		public boolean canPutStackInSlot(ItemStack stack) {
			return inv.canPlaceItemThroughFace(slot, stack, side) && inv.canPlaceItem(slot, stack);
		}

		@Override
		public boolean canTakeStackFromSlot(ItemStack stack) {
			return inv.canTakeItemThroughFace(slot, stack, side);
		}

		@Override
		public boolean isItemValidForSlot(ItemStack stack) {
			return inv.canPlaceItem(slot, stack);
		}

		@Override
		public ItemStack decreaseStackInSlot(int amount) {
			return inv.removeItem(slot, amount);
		}

		@Override
		public int getIndex() {
			return slot;
		}

	}
}
