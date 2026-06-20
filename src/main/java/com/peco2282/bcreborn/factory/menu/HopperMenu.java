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
package com.peco2282.bcreborn.factory.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.factory.FactoryMenuTypes;
import com.peco2282.bcreborn.factory.block.entity.HopperBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HopperMenu extends BuildCraftMenu<HopperMenu> {
  private final HopperBlockEntity tile;

  public HopperMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
    this(id, playerInv, getBlockEntity(playerInv, buf));
  }

  public HopperMenu(int id, Inventory playerInv, HopperBlockEntity tile) {
    super(FactoryMenuTypes.HOPPER.get(), id, playerInv);
    this.tile = tile;

    // Hopper inventory
    this.addSlot(new Slot(tile, 0, 62, 18));
    this.addSlot(new Slot(tile, 1, 80, 18));
    this.addSlot(new Slot(tile, 2, 98, 18));
    this.addSlot(new Slot(tile, 3, 80, 36));

    // Player inventory
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 71 + i * 18));
      }
    }

    // Player hotbar
    for (int i = 0; i < 9; i++) {
      this.addSlot(new Slot(playerInv, i, 8 + i * 18, 129));
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return tile.stillValid(player);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);
    if (slot.hasItem()) {
      ItemStack itemstack1 = slot.getItem();
      itemstack = itemstack1.copy();
      if (index < 4) {
        if (!this.moveItemStackTo(itemstack1, 4, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveItemStackTo(itemstack1, 0, 4, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.setByPlayer(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }

    return itemstack;
  }
}
