/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.silicon.menu;

import com.peco2282.bcreborn.common.gui.slots.SlotPhantom;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.silicon.SiliconMenuTypes;
import com.peco2282.bcreborn.silicon.block.entity.AdvancedCraftingTableBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class AdvancedCraftingTableMenu extends BuildCraftMenu<AdvancedCraftingTableMenu> {

	private final AdvancedCraftingTableBlockEntity workbench;

	public AdvancedCraftingTableMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
		this(windowId, playerInventory, (AdvancedCraftingTableBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()));
	}

	public AdvancedCraftingTableMenu(int windowId, Inventory playerInventory, AdvancedCraftingTableBlockEntity table) {
		super(SiliconMenuTypes.ADVANCED_CRAFTING_TABLE.get(), windowId, playerInventory);
		this.workbench = table;

		// 1.7.10 のロジックを再現。スロット番号などは調整が必要かもしれない
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				// CraftingSlots
				addSlot(new SlotPhantom(new SimpleContainer(9), x + y * 3, 33 + x * 18, 16 + y * 18));
			}
		}

		// Output slot
		addSlot(new Slot(new SimpleContainer(1), 0, 127, 34));

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 5; x++) {
				addSlot(new Slot(workbench, x + y * 5, 15 + x * 18, 85 + y * 18));
			}
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new Slot(workbench, 15 + x + y * 3, 109 + x * 18, 85 + y * 18));
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int k1 = 0; k1 < 9; k1++) {
				addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 153 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; i1++) {
			addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 211));
		}
	}

	@Override
	public boolean stillValid(Player var1) {
		return workbench.stillValid(var1);
	}
}
