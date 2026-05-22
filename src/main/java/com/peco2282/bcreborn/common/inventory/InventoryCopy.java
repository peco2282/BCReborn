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

import java.util.Arrays;

/**
 * Creates a deep copy of an existing Container.
 *
 * Useful for performing inventory manipulations and then examining the results
 * without affecting the original inventory.
 */
public class InventoryCopy implements Container {

	private Container orignal;
	private ItemStack[] contents;

	public InventoryCopy(Container orignal) {
		this.orignal = orignal;
		contents = new ItemStack[orignal.getContainerSize()];
		for (int i = 0; i < contents.length; i++) {
			ItemStack stack = orignal.getItem(i);
			if (stack != null && !stack.isEmpty()) {
				contents[i] = stack.copy();
			} else {
				contents[i] = ItemStack.EMPTY;
			}
		}
	}

	@Override
	public int getContainerSize() {
		return contents.length;
	}

	@Override
	public boolean isEmpty() {
		return Arrays.stream(contents).allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int i) {
		return contents[i];
	}

	@Override
	public ItemStack removeItem(int i, int j) {
		if (contents[i] != null && !contents[i].isEmpty()) {
			ItemStack itemstack = contents[i].split(j);
			return itemstack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = contents[slot];
		contents[slot] = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setItem(int i, ItemStack itemstack) {
		contents[i] = itemstack;
	}

	@Override
	public int getMaxStackSize() {
		return orignal.getMaxStackSize();
	}

	@Override
	public boolean stillValid(Player entityplayer) {
		return true;
	}

	@Override
	public void startOpen(Player player) {
	}

	@Override
	public void stopOpen(Player player) {
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return orignal.canPlaceItem(slot, stack);
	}

	public ItemStack[] getItemStacks() {
		return contents;
	}

	@Override
	public void setChanged() {

	}

	@Override
	public void clearContent() {
		Arrays.fill(contents, ItemStack.EMPTY);
	}
}
