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
package com.peco2282.bcreborn.builders.menu;

import com.peco2282.bcreborn.builders.BuildersMenuTypes;
import com.peco2282.bcreborn.builders.block.entity.BuilderBlockEntity;
import com.peco2282.bcreborn.common.gui.widgets.ScrollbarWidget;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class BuilderMenu extends BuildCraftMenu<BuilderMenu> {
  protected ScrollbarWidget scrollbarWidget;
  protected BuilderBlockEntity builder;

  // Client constructor
  public BuilderMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, (BuilderBlockEntity) playerInventory.player.level().getBlockEntity(data.readBlockPos()));
  }

  // Server constructor
  public BuilderMenu(int windowId, Inventory playerInventory, BuilderBlockEntity builder) {
    super(BuildersMenuTypes.BUILDER.get(), windowId, playerInventory);
    this.builder = builder;

    this.scrollbarWidget = new ScrollbarWidget(172, 17, 18, 0, 108);
    this.scrollbarWidget.hidden = true;
    this.addWidget(scrollbarWidget);

    // Main slot
    this.addSlot(new Slot(builder.getInventory(), 0, 80, 27));

    // Builder inventory
    for (int k = 0; k < 3; k++) {
      for (int j1 = 0; j1 < 9; j1++) {
        this.addSlot(new Slot(builder.getInventory(), 1 + j1 + k * 9, 8 + j1 * 18, 72 + k * 18));
      }
    }

    // Player inventory
    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
      }
    }

    // Hotbar
    for (int x = 0; x < 9; x++) {
      this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
    }

    if (!builder.getLevel().isClientSide) {
      builder.updateRequirementsOnGuiOpen(playerInventory.player);
      builder.addGuiWatcher(playerInventory.player);
    }
  }

  @Override
  public void removed(@NotNull Player player) {
    super.removed(player);
    if (!builder.getLevel().isClientSide) {
      builder.removeGuiWatcher(player);
    }
  }

  public ScrollbarWidget getScrollbarWidget() {
    return scrollbarWidget;
  }

  public BuilderBlockEntity getBuilder() {
    return builder;
  }

  @Override
  public boolean stillValid(@NotNull Player player) {
    return builder.stillValid(player);
  }
}
