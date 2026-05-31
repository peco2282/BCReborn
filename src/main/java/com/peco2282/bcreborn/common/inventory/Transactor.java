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

import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;

import net.minecraft.core.Direction;

public abstract class Transactor implements ITransactor {

	@Override
	public ItemStack add(ItemStack stack, Direction orientation, boolean doAdd) {
		ItemStack added = stack.copy();
		added.setCount(inject(stack, orientation, doAdd));
		return added;
	}

	public abstract int inject(ItemStack stack, Direction orientation, boolean doAdd);

	public static ITransactor getTransactorFor(Object object) {
		if (object instanceof WorldlyContainer) {
			return new TransactorSimple((WorldlyContainer) object);
		} else if (object instanceof Container) {
			return new TransactorSimple((Container) object);
		}

		return null;
	}
}
