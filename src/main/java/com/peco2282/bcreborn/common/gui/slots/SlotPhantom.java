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

public class SlotPhantom extends SlotBase implements IPhantomSlot {

	public SlotPhantom(Container iinventory, int slotIndex, int posX, int posY) {
		super(iinventory, slotIndex, posX, posY);
	}

	@Override
	public boolean canAdjust() {
		return true;
	}

	@Override
	public boolean mayPickup(Player par1EntityPlayer) {
		return false;
	}
}
