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

import com.peco2282.bcreborn.api.crops.CropManager;
import com.peco2282.bcreborn.api.transport.IStripesActivator;
import com.peco2282.bcreborn.api.transport.IStripesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StripesHandlerPlant implements IStripesHandler {
	@Override
	public StripesHandlerType getType() {
		return StripesHandlerType.ITEM_USE;
	}

	@Override
	public boolean shouldHandle(ItemStack stack) {
		return CropManager.isSeed(stack);
	}

	@Override
	public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player, IStripesActivator activator) {
		BlockPos target = pos.below();
		if (CropManager.canSustainPlant(world, stack, target)) {
			if (CropManager.plantCrop(world, player, stack, target)) {
				if (!stack.isEmpty()) {
					activator.sendItem(stack, direction.getOpposite());
				}
				return true;
			}
		}
		return false;
	}
}
