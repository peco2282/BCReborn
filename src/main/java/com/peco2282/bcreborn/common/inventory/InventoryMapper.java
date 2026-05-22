/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Wrapper class used to specify part of an existing inventory to be treated as
 * a complete inventory. Used primarily to map a side of an ISidedInventory, but
 * it is also helpful for complex inventories such as the Tunnel Bore.
 */
public class InventoryMapper implements Container {

	private final Container inv;
	private final int start;
	private final int size;
	private int stackSizeLimit = -1;
	private boolean checkItems = true;

	/**
	 * Creates a new InventoryMapper
	 *
	 * @param inv The backing inventory
	 * @param start The starting index
	 * @param size The size of the new inventory, take care not to exceed the
	 * end of the backing inventory
	 */
	public InventoryMapper(Container inv, int start, int size) {
		this(inv, start, size, true);
	}

	public InventoryMapper(Container inv, int start, int size, boolean checkItems) {
		this.inv = inv;
		this.start = start;
		this.size = size;
		this.checkItems = checkItems;
	}

	public Container getBaseInventory() {
		return inv;
	}

	@Override
	public int getContainerSize() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < size; i++) {
			if (!getItem(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return inv.getItem(start + slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		return inv.removeItem(start + slot, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return inv.removeItemNoUpdate(start + slot);
	}

	@Override
	public void setItem(int slot, ItemStack itemstack) {
		inv.setItem(start + slot, itemstack);
	}

	public void setStackSizeLimit(int limit) {
		stackSizeLimit = limit;
	}

	@Override
	public int getMaxStackSize() {
		return stackSizeLimit > 0 ? stackSizeLimit : inv.getMaxStackSize();
	}

	@Override
	public boolean stillValid(Player entityplayer) {
		return inv.stillValid(entityplayer);
	}

	@Override
	public void startOpen(Player player) {
		inv.startOpen(player);
	}

	@Override
	public void stopOpen(Player player) {
		inv.stopOpen(player);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (checkItems) {
			return inv.canPlaceItem(start + slot, stack);
		}
		return true;
	}

	@Override
	public void setChanged() {
		inv.setChanged();
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < size; i++) {
			setItem(i, ItemStack.EMPTY);
		}
	}
}
