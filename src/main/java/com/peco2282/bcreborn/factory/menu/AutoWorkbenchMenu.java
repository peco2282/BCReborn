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

import com.peco2282.bcreborn.common.gui.slots.SlotOutput;
import com.peco2282.bcreborn.common.gui.slots.SlotPhantom;
import com.peco2282.bcreborn.common.gui.slots.SlotUntouchable;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.factory.FactoryMenuTypes;
import com.peco2282.bcreborn.factory.block.entity.AutoWorkbenchBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AutoWorkbenchMenu extends BuildCraftMenu<AutoWorkbenchMenu> {

  private final AutoWorkbenchBlockEntity tile;
  private final ContainerData data;
  private ItemStack prevOutput;


  public AutoWorkbenchMenu(int p_38852_, Inventory p_38853_, FriendlyByteBuf buf) {
    this(p_38852_, p_38853_, (AutoWorkbenchBlockEntity) p_38853_.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(1));
  }

  public AutoWorkbenchMenu(int id, Inventory playerInv, AutoWorkbenchBlockEntity tile, ContainerData data) {
    super(FactoryMenuTypes.AUTO_WORKBENCH.get(), id, playerInv);
    this.tile = tile;
    this.data = data;
    addDataSlots(data);

    addSlot(new SlotUntouchable(tile.getCraftResult(), 0, 93, 27));
    addSlot(new SlotOutput(tile, AutoWorkbenchBlockEntity.SLOT_RESULT, 124, 35));
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 3; x++) {
        addSlot(new SlotWorkbench(tile, 10 + x + y * 3, 30 + x * 18, 17 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(tile, x, 8 + x * 18, 84));
    }

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 115 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new Slot(playerInv, x, 8 + x * 18, 173));
    }

    onCraftMatrixChanged(tile);
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    if (tile.getLevel() != null && !tile.getLevel().isClientSide) {
      this.data.set(0, tile.progress);

      ItemStack output = tile.getCraftResult().getItem(0);
      if (output != prevOutput) {
        prevOutput = output;
        onCraftMatrixChanged(tile.craftMatrix);
      }
    }
  }

  public int getProgress() {
    return this.data.get(0);
  }

  public final void onCraftMatrixChanged(Container inv) {
    if (tile != null) {
      tile.craftMatrix.rebuildCache();
      ItemStack output = tile.craftMatrix.getRecipeOutput();
      tile.getCraftResult().setItem(0, output);
    }
  }

  @Override
  public void clicked(int i, int j, ClickType modifier, Player entityplayer) {
    super.clicked(i, j, modifier, entityplayer);
    onCraftMatrixChanged(tile.craftMatrix);
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return tile.stillValid(p_38874_);
  }

  public static class SlotWorkbench extends SlotPhantom {
    public SlotWorkbench(Container iinventory, int slotIndex, int posX, int posY) {
      super(iinventory, slotIndex, posX, posY);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
      return !stack.isEmpty() && !stack.getItem().hasCraftingRemainingItem(stack);
    }

    @Override
    public boolean mayPickup(Player player) {
      return false;
    }
  }

}
