/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.core.list;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.core.item.ListItem;
import com.peco2282.bcreborn.core.MenuTypesCore;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ListOldMenu extends BuildCraftMenu<ListOldMenu> {

	public ListHandlerOld.StackLine[] lines;
	private Player player;

	public ListOldMenu(int containerId, Player iPlayer) {
		super(MenuTypesCore.LIST_OLD.get(), containerId, iPlayer.getInventory());

		player = iPlayer;

		lines = ListHandlerOld.getLines(player.getMainHandItem());

		for (int sy = 0; sy < 3; sy++) {
			for (int sx = 0; sx < 9; sx++) {
				addSlot(new Slot(player.getInventory(), sx + sy * 9 + 9, 8 + sx * 18, 153 + sy * 18));
			}
		}

		for (int sx = 0; sx < 9; sx++) {
			addSlot(new Slot(player.getInventory(), sx, 8 + sx * 18, 211));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	public void setStack(final int lineIndex, final int slotIndex, final ItemStack stack) {
		lines[lineIndex].setStack(slotIndex, stack);
		ListHandlerOld.saveLine(player.getMainHandItem(), lines[lineIndex], lineIndex);

		if (player.level().isClientSide) {
			BCNetworkManager.sendListSetStack(lineIndex, slotIndex, stack);
		}
	}

	public void switchButton(final int lineIndex, final int button) {
		if (button == 0) {
			lines[lineIndex].oreWildcard = false;
			lines[lineIndex].subitemsWildcard = !lines[lineIndex].subitemsWildcard;
		} else if (button == 1 && lines[lineIndex].isOre) {
			lines[lineIndex].subitemsWildcard = false;
			lines[lineIndex].oreWildcard = !lines[lineIndex].oreWildcard;
		}

		ListHandlerOld.saveLine(player.getMainHandItem(), lines[lineIndex], lineIndex);

		if (player.level().isClientSide) {
			BCNetworkManager.sendListSwitchButton(lineIndex, button);
		}
	}

	public void setLabel(final String text) {
		ListItem.saveLabel(player.getMainHandItem(), Component.literal(text));

		if (player.level().isClientSide) {
			BCNetworkManager.sendListSetLabel(text);
		}
	}
}
