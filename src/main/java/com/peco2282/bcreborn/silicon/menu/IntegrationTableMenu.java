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
package com.peco2282.bcreborn.silicon.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.silicon.SiliconMenuTypes;
import com.peco2282.bcreborn.silicon.block.entity.IntegrationTableBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class IntegrationTableMenu extends BuildCraftMenu<IntegrationTableMenu> {
  public static final int[] SLOT_X = {
    44, 44, 69, 69, 69, 44, 19, 19, 19
  };
  public static final int[] SLOT_Y = {
    49, 24, 24, 49, 74, 74, 74, 49, 24
  };

  private final IntegrationTableBlockEntity table;

  public IntegrationTableMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, getBlockEntity(playerInventory, data));
  }

  public IntegrationTableMenu(int windowId, Inventory playerInventory, IntegrationTableBlockEntity table) {
    super(SiliconMenuTypes.INTEGRATION_TABLE.get(), windowId, playerInventory);
    this.table = table;

    for (int i = 0; i < 9; i++) {
      addSlot(new Slot(table, i, SLOT_X[i], SLOT_Y[i]));
    }

    addSlot(new Slot(table, 9, 138, 49));
    addSlot(new Slot(new SimpleContainer(1), 0, 101, 36));

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 109 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(playerInventory, x, 8 + x * 18, 167));
    }
  }

  @Override
  public boolean stillValid(Player var1) {
    return table.stillValid(var1);
  }
}
