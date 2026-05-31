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
import com.peco2282.bcreborn.silicon.block.entity.StampingTableBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class StampingTableMenu extends BuildCraftMenu<StampingTableMenu> {

  private final StampingTableBlockEntity table;

  public StampingTableMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, (StampingTableBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()));
  }

  public StampingTableMenu(int windowId, Inventory playerInventory, StampingTableBlockEntity table) {
    super(SiliconMenuTypes.STAMPING_TABLE.get(), windowId, playerInventory);
    this.table = table;

    addSlot(new Slot(table, 0, 15, 18));
    addSlot(new Slot(table, 1, 143, 18));
    addSlot(new Slot(table, 2, 111, 45));
    addSlot(new Slot(table, 3, 129, 45));
    addSlot(new Slot(table, 4, 147, 45));

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 69 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(playerInventory, x, 8 + x * 18, 127));
    }
  }

  @Override
  public boolean stillValid(Player var1) {
    return table.stillValid(var1);
  }
}
