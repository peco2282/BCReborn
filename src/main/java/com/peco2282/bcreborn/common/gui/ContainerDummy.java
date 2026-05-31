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


import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ContainerDummy extends BuildCraftMenu<ContainerDummy> {
	public ContainerDummy(@Nullable MenuType<ContainerDummy> p_38851_, int p_38852_, Inventory p_38853_) {
		super(p_38851_, p_38852_, p_38853_);
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player p_38874_) {
		return false;
	}
	public static class DummyScreen extends Screen {
		public DummyScreen() {
			super(Component.literal("Dummy Screen"));
		}
	}
}
