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
package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.api.lists.ListRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ListMatchHandlerClass extends ListMatchHandler {
	@Override
	public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
		if (type == Type.TYPE) {
			Class<? extends Item> kl = stack.getItem().getClass();
			return ListRegistry.itemClassAsType.contains(kl) && kl.equals(target.getItem().getClass());
		}
		return false;
	}

	@Override
	public boolean isValidSource(Type type, ItemStack stack) {
		if (type == Type.TYPE) {
			Class<? extends Item> kl = stack.getItem().getClass();
			return ListRegistry.itemClassAsType.contains(kl);
		}
		return false;
	}
}
