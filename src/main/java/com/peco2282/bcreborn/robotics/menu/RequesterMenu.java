/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.robotics.MenuTypesRobotics;
import com.peco2282.bcreborn.robotics.block.entity.RequesterBlockEntity;
import com.peco2282.bcreborn.robotics.screen.RequesterScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RequesterMenu extends BuildCraftMenu<RequesterMenu> {

	public RequesterScreen gui;
	public ItemStack[] requests = new ItemStack[RequesterBlockEntity.NB_ITEMS];
	private final RequesterBlockEntity requester;

	public RequesterMenu(int id, Inventory playerInventory, RequesterBlockEntity iRequester) {
		super(MenuTypesRobotics.REQUESTER.get(), id, playerInventory);
		this.requester = iRequester;
        if (this.requester == null) {
            throw new IllegalArgumentException("RequesterBlockEntity is null at pos");
        }

		for (int x = 0; x < 4; ++x) {
			for (int y = 0; y < 5; ++y) {
				addSlot(new Slot(iRequester, x * 5 + y, 117 + x * 18, 7 + y * 18));
			}
		}

		// Player inventory
		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 9; k1++) {
				addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 19 + k1 * 18, 101 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; i1++) {
			addSlot(new Slot(playerInventory, i1, 19 + i1 * 18, 159));
		}

		for (int i = 0; i < requests.length; i++) {
			requests[i] = ItemStack.EMPTY;
		}
	}

	public RequesterMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
		this(id, playerInv, (RequesterBlockEntity) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public void getRequestList() {
		BCNetworkManager.sendRequestRequesterList(requester.getBlockPos());
	}

	public RequesterBlockEntity getRequester() {
		return requester;
	}
}
