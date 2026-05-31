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
package com.peco2282.bcreborn.silicon.screen;

import com.peco2282.bcreborn.silicon.block.entity.ChargingTableBlockEntity;
import com.peco2282.bcreborn.silicon.menu.ChargingTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChargingTableScreen extends LaserTableScreen<ChargingTableMenu> {

  public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("bcrebornsilicon", "textures/gui/charging_table.png");

  public ChargingTableScreen(ChargingTableMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title, (ChargingTableBlockEntity) container.getSlot(0).container);
    this.imageWidth = 176;
    this.imageHeight = 132;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  @Override
  protected ResourceLocation getMenuTexture() {
    return TEXTURE;
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
  }
}
