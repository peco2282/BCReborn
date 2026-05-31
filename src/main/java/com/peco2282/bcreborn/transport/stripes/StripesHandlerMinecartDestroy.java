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
package com.peco2282.bcreborn.transport.stripes;

import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StripesHandlerMinecartDestroy implements IStripesHandler {

	@Override
	public StripesHandlerType getType() {
		return StripesHandlerType.BLOCK_BREAK;
	}

	@Override
	public boolean shouldHandle(ItemStack stack) {
		return true;
	}

	@Override
	public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player,
						  IStripesActivator activator) {
		AABB box = new AABB(pos);
		List<AbstractMinecart> minecarts = world.getEntitiesOfClass(AbstractMinecart.class, box);
		if (minecarts.isEmpty()) {
			return false;
		}

		Collections.shuffle(minecarts);
		AbstractMinecart cart = minecarts.get(0);
		if (cart instanceof Container container) {
			for (int i = 0; i < container.getContainerSize(); i++) {
				ItemStack s = container.getItem(i);
				if (!s.isEmpty()) {
					container.setItem(i, ItemStack.EMPTY);
					if (container.getItem(i).isEmpty()) {
						activator.sendItem(s, direction.getOpposite());
					}
				}
			}
		}

		ItemStack cartItem = cart.getPickResult();
		if (cartItem == null || cartItem.isEmpty()) {
			// In 1.20.1 AbstractMinecart doesn't have getCartItem(), pickResult should work
			// if it fails we might need to check specific types or use a fallback.
			// Using reflection as AT might not be picked up correctly in all environments
			try {
				java.lang.reflect.Method method = AbstractMinecart.class.getDeclaredMethod("getDropItem");
				method.setAccessible(true);
				cartItem = new ItemStack((net.minecraft.world.item.Item) method.invoke(cart));
			} catch (Exception e) {
				cartItem = ItemStack.EMPTY;
			}
		}
		
		cart.discard();
		activator.sendItem(cartItem, direction.getOpposite());
		return true;
	}
}
