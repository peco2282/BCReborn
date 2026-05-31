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
