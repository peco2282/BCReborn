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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class StripesHandlerUse implements IStripesHandler {
	public static final List<Item> items = new ArrayList<Item>();

	@Override
	public StripesHandlerType getType() {
		return StripesHandlerType.ITEM_USE;
	}

	@Override
	public boolean shouldHandle(ItemStack stack) {
		return items.contains(stack.getItem());
	}

	@Override
	public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
		BlockPos target = pos.relative(direction);
		BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(target), direction.getOpposite(), target, false);
		UseOnContext context = new UseOnContext(world, player, InteractionHand.MAIN_HAND, stack, hitResult);

		if (stack.useOn(context).consumesAction()) {
			if (!stack.isEmpty()) {
				activator.sendItem(stack, direction.getOpposite());
			}
			return true;
		}
		return false;
	}

}
