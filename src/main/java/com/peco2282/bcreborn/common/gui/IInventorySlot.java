/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * More dynamic slot displaying an inventory fluid at specified position in
 * the passed Container
 */
public class IInventorySlot extends AdvancedSlot {
	private final Container tile;
	private final int slot;

	public IInventorySlot(GuiAdvancedInterface<?> gui, int x, int y, Container tile, int slot) {
		super(gui, x, y);
		this.tile = tile;
		this.slot = slot;
	}

	@Override
	public ItemStack getItemStack() {
		return tile.getItem(slot);
	}
}
