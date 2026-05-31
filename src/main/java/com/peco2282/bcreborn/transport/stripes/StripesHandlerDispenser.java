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

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.ArrayList;
import java.util.List;

public class StripesHandlerDispenser implements IStripesHandler {
	public static final List<Object> items = new ArrayList<Object>();

	@Override
	public StripesHandlerType getType() {
		return StripesHandlerType.ITEM_USE;
	}

	@Override
	public boolean shouldHandle(ItemStack stack) {
		if (items.contains(stack.getItem())) {
			return true;
		}

		Class<?> c = stack.getItem().getClass();
		while (c != Item.class) {
			if (items.contains(c)) {
				return true;
			}
			c = c.getSuperclass();
		}
		return false;
	}

	@Override
	public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
		if (!(world instanceof ServerLevel serverLevel)) {
			return false;
		}
		Position origin = new Position(pos.getX(), pos.getY(), pos.getZ(), direction);
		origin.moveBackwards(1.0D);

		BlockPos sourcePos = origin.toBlockPos();
		BlockSourceImpl source = new BlockSourceImpl(serverLevel, sourcePos);
		DispenseItemBehavior behaviour = DispenserBlock.DISPENSER_REGISTRY.get(stack.getItem());
		if (behaviour != null && behaviour != DispenseItemBehavior.NOOP) {
			ItemStack output = behaviour.dispense(source, stack.copy());
			if (!output.isEmpty()) {
				activator.sendItem(output, direction.getOpposite());
			}
			return true;
		}
		return false;
	}
}
