/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.slots;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotUntouchable extends SlotBase implements IPhantomSlot {

	public SlotUntouchable(Container contents, int id, int x, int y) {
		super(contents, id, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		return false;
	}

	@Override
	public boolean mayPickup(Player par1EntityPlayer) {
		return false;
	}

	@Override
	public boolean canAdjust() {
		return false;
	}

	@Override
	public boolean canShift() {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isActive() {
		return false;
	}
}
