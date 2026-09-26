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

import com.peco2282.bcreborn.common.gui.slots.SlotOutput;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.silicon.block.entity.ProgrammingTableBlockEntity;
import com.peco2282.bcreborn.silicon.SiliconMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ProgrammingTableMenu extends BuildCraftMenu<ProgrammingTableMenu> {
  private final ProgrammingTableBlockEntity table;

  public ProgrammingTableMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, getBlockEntity(playerInventory, data));
  }

  public ProgrammingTableMenu(int windowId, Inventory playerInventory, ProgrammingTableBlockEntity table) {
    super(SiliconMenuTypes.PROGRAMMING_TABLE.get(), windowId, playerInventory);
    this.table = table;
    addDataSlots(new LaserTableData(table));

    addSlot(new Slot(table, 0, 8, 36));
    addSlot(new SlotOutput(table, 1, 8, 90));
    addSlot(new Slot(table, 2, 80, 54) {
      @Override public boolean mayPlace(ItemStack stack) { return false; }
      @Override public boolean mayPickup(Player player) { return false; }
    });

    for (int l = 0; l < 3; l++) {
      for (int k1 = 0; k1 < 9; k1++) {
        addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 123 + l * 18));
      }
    }

    for (int i1 = 0; i1 < 9; i1++) {
      addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 181));
    }
  }

  @Override
  public boolean clickMenuButton(Player player, int id) {
    if (!stillValid(player) || (id != 0 && id != 1)) return false;
    if (!player.level().isClientSide) {
      table.cycleOption(id == 0 ? -1 : 1);
      broadcastChanges();
    }
    return true;
  }

  @Override
  public boolean stillValid(Player entityplayer) {
    return table.stillValid(entityplayer);
  }
}
