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

import com.peco2282.bcreborn.core.item.MapLocationItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.ContainerData;
import com.peco2282.bcreborn.common.gui.slots.SlotOutput;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.robotics.RoboticsMenuTypes;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import com.peco2282.bcreborn.robotics.screen.ZonePlanScreen;
import com.peco2282.bcreborn.robotics.zone.ZonePlan;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

/**
 * Port of the original {@code ContainerZonePlan}: map input / output on the right, the import slot below the map.
 * The zone being painted ({@link #currentAreaSelection}) is edited on the client and saved to the server.
 */
public class ZonePlanMenu extends BuildCraftMenu<ZonePlanMenu> {
  private final ZonePlanBlockEntity map;
  private final ContainerData progress;
  public ZonePlan currentAreaSelection;
  public ZonePlanScreen gui;

  public ZonePlanMenu(int id, Inventory playerInventory, ZonePlanBlockEntity iZonePlan) {
    this(id, playerInventory, iZonePlan, new ContainerData() {
      @Override
      public int get(int index) {
        return iZonePlan.progress;
      }

      @Override
      public void set(int index, int value) {
        iZonePlan.progress = (short) value;
      }

      @Override
      public int getCount() {
        return 1;
      }
    });
  }

  private ZonePlanMenu(int id, Inventory playerInventory, ZonePlanBlockEntity iZonePlan, ContainerData progress) {
    super(RoboticsMenuTypes.ZONE_PLAN.get(), id, playerInventory);
    this.map = iZonePlan;
    if (this.map == null) {
      throw new IllegalArgumentException("ZonePlanBlockEntity is null at pos");
    }
    this.progress = progress;
    addDataSlots(progress);

    addSlot(new Slot(iZonePlan, ZonePlanBlockEntity.SLOT_INPUT, 233, 9) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof MapLocationItem;
      }
    });
    addSlot(new SlotOutput(iZonePlan, ZonePlanBlockEntity.SLOT_OUTPUT, 233, 57));
    addSlot(new Slot(iZonePlan, ZonePlanBlockEntity.SLOT_IMPORT, 8, 125) {
      @Override
      public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof MapLocationItem;
      }
    });

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
    this(id, playerInv, (ZonePlanBlockEntity) playerInv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(1));
  }

  public ZonePlanBlockEntity getTile() {
    return map;
  }

  /** Map writing progress, 0..{@link ZonePlanBlockEntity#CRAFT_TIME}. */
  public int getProgress() {
    return progress.get(0);
  }

  @Override
  public boolean stillValid(Player player) {
    return map.stillValid(player);
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
