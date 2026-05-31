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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StripesHandlerArrow implements IStripesHandler {

	@Override
	public StripesHandlerType getType() {
		return StripesHandlerType.ITEM_USE;
	}

	@Override
	public boolean shouldHandle(ItemStack stack) {
		return stack.getItem() == Items.ARROW;
	}

	@Override
	public boolean handle(Level world, BlockPos pos, Direction direction, ItemStack stack, Player player,
						  IStripesActivator activator) {

		Arrow entityArrow = new Arrow(world, player);
		entityArrow.setPos(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
		entityArrow.setBaseDamage(3);
		entityArrow.setKnockback(1);
		entityArrow.setDeltaMovement(new Vec3(
				direction.getStepX() * 1.8d + world.random.nextGaussian() * 0.007499999832361937D,
				direction.getStepY() * 1.8d + world.random.nextGaussian() * 0.007499999832361937D,
				direction.getStepZ() * 1.8d + world.random.nextGaussian() * 0.007499999832361937D
		));
		world.addFreshEntity(entityArrow);

		stack.shrink(1);
		if (!stack.isEmpty()) {
			activator.sendItem(stack, direction.getOpposite());
		}

		return true;
	}

}
