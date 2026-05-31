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

import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class InventoryWrapper implements WorldlyContainer {

	Container inventory;

	public InventoryWrapper(Container inventory) {
		this.inventory = inventory;
	}

	/* DIRECT MAPPING */
	@Override
	public int getContainerSize() {
		return inventory.getContainerSize();
	}

	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public ItemStack getItem(int slotIndex) {
		return inventory.getItem(slotIndex);
	}

	@Override
	public ItemStack removeItem(int slotIndex, int amount) {
		return inventory.removeItem(slotIndex, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int slotIndex) {
		return inventory.removeItemNoUpdate(slotIndex);
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemstack) {
		inventory.setItem(slotIndex, itemstack);
	}

	@Override
	public int getMaxStackSize() {
		return inventory.getMaxStackSize();
	}

	@Override
	public void setChanged() {
		inventory.setChanged();
	}

	@Override
	public boolean stillValid(Player entityplayer) {
		return inventory.stillValid(entityplayer);
	}

	@Override
	public void startOpen(Player player) {
		inventory.startOpen(player);
	}

	@Override
	public void stopOpen(Player player) {
		inventory.stopOpen(player);
	}

	@Override
	public boolean canPlaceItem(int slotIndex, ItemStack itemstack) {
		return inventory.canPlaceItem(slotIndex, itemstack);
	}

	/* STATIC HELPER */
	public static WorldlyContainer getWrappedInventory(Object inventory) {
		if (inventory instanceof WorldlyContainer) {
			return (WorldlyContainer) inventory;
		} else if (inventory instanceof Container) {
			return new InventoryWrapperSimple((Container) inventory);
		} else {
			return null;
		}
	}

	@Override
	public void clearContent() {
		inventory.clearContent();
	}
}
