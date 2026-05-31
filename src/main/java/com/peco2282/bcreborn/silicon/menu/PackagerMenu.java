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

import com.peco2282.bcreborn.common.gui.slots.SlotPhantom;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.silicon.SiliconMenuTypes;
import com.peco2282.bcreborn.silicon.block.entity.PackagerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PackagerMenu extends BuildCraftMenu<PackagerMenu> {
  private final PackagerBlockEntity tile;

  public PackagerMenu(int windowId, Inventory inventory, FriendlyByteBuf data) {
    this(windowId, inventory, getBlockEntity(inventory, data));
  }

  public PackagerMenu(int windowId, Inventory inventory, PackagerBlockEntity t) {
    super(SiliconMenuTypes.PACKAGER.get(), windowId, inventory);
    this.tile = t;

    // sort in order of shift-click!
    addSlot(new Slot(tile, 9, 124, 7));

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(tile, x, 8 + x * 18, 84));
    }

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        addSlot(new SlotPackager(tile, x + y * 3, 30 + x * 18, 17 + y * 18));
      }
    }

    // addSlot(new Slot(tile, 10, 108, 31));
    addSlot(new Slot(tile, 11, 123, 59));

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 115 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(inventory, x, 8 + x * 18, 173));
    }
  }

  @Override
  public void clicked(int slotNum, int mouseButton, ClickType clickType, Player player) {
    Slot slot = slotNum < 0 ? null : getSlot(slotNum);
    if (slot instanceof SlotPackager) {
      slotClickPhantom(slot, slotNum, mouseButton, clickType, player);
      return;
    }
    super.clicked(slotNum, mouseButton, clickType, player);
  }

  @Override
  public boolean stillValid(Player entityplayer) {
    return tile.stillValid(entityplayer);
  }

  public static class SlotPackager extends SlotPhantom {
    public SlotPackager(Container iinventory, int slotIndex, int posX, int posY) {
      super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
      return !stack.isEmpty();
    }

    @Override
    public int getMaxStackSize() {
      return 1;
    }
  }

}
