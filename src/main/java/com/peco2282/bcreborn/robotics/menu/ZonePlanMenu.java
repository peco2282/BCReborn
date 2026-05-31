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
package com.peco2282.bcreborn.robotics.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.robotics.MenuTypesRobotics;
import com.peco2282.bcreborn.robotics.ZonePlan;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import com.peco2282.bcreborn.robotics.screen.ZonePlanScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public class ZonePlanMenu extends BuildCraftMenu<ZonePlanMenu> {
  private final ZonePlanBlockEntity map;
  public ZonePlan currentAreaSelection;
  public ZonePlanScreen gui;

  public ZonePlanMenu(int id, Inventory playerInventory, ZonePlanBlockEntity iZonePlan) {
    super(MenuTypesRobotics.ZONE_PLAN.get(), id, playerInventory);
    this.map = iZonePlan;
    if (this.map == null) {
      throw new IllegalArgumentException("ZonePlanBlockEntity is null at pos");
    }

    addSlot(new Slot(iZonePlan, 0, 233, 9));
    // TODO: addSlot(new SlotOutput(iZonePlan, 1, 233, 57));
    addSlot(new Slot(iZonePlan, 1, 233, 57));
    addSlot(new Slot(iZonePlan, 2, 8, 125));

    // Player inventory
    for (int l = 0; l < 3; l++) {
      for (int k1 = 0; k1 < 9; k1++) {
        addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 88 + k1 * 18, 146 + l * 18));
      }
    }

    for (int i1 = 0; i1 < 9; i1++) {
      addSlot(new Slot(playerInventory, i1, 88 + i1 * 18, 204));
    }
  }

  public ZonePlanMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
    this(id, playerInv, (ZonePlanBlockEntity) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
  }

  public ZonePlanBlockEntity getTile() {
    return map;
  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  public void loadArea(final int index) {
    BCNetworkManager.sendRequestZonePlanLoadArea(map.getBlockPos(), index);
  }

  public void saveArea(final int index) {
    BCNetworkManager.sendRequestZonePlanSaveArea(map.getBlockPos(), index, currentAreaSelection);
  }

  public void setName(String name) {
    BCNetworkManager.sendRequestZonePlanSetName(map.getBlockPos(), name);
  }

  public void computeMap(int cx, int cz, int width, int height, float blocksPerPixel) {
    BCNetworkManager.sendRequestZonePlanComputeMap(map.getBlockPos(), cx, cz, width, height, blocksPerPixel);
  }
}
