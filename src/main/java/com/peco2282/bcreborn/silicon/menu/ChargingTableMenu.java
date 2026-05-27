/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.silicon.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.silicon.SiliconMenuTypes;
import com.peco2282.bcreborn.silicon.block.entity.ChargingTableBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class ChargingTableMenu extends BuildCraftMenu<ChargingTableMenu> {

	private final ChargingTableBlockEntity table;

	public ChargingTableMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
		this(windowId, playerInventory, (ChargingTableBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()));
	}

	public ChargingTableMenu(int windowId, Inventory playerInventory, ChargingTableBlockEntity table) {
		super(SiliconMenuTypes.CHARGING_TABLE.get(), windowId, playerInventory);
		this.table = table;

		addSlot(new Slot(table, 0, 80, 18));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 50 + y * 18));
			}
		}

		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(playerInventory, x, 8 + x * 18, 108));
		}
	}

	@Override
	public boolean stillValid(Player var1) {
		return table.stillValid(var1);
	}
}
