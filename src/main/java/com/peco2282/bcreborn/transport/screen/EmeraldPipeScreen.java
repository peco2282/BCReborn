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
package com.peco2282.bcreborn.transport.screen;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.transport.menu.EmeraldPipeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EmeraldPipeScreen extends BuildCraftScreen<EmeraldPipeMenu> {
  private static final ResourceLocation TEXTURE = BCRebornTransport.location("textures/gui/pipe_emerald.png");
  private static final ResourceLocation TEXTURE_BUTTON = BCRebornTransport.location("textures/gui/pipe_emerald_button.png");

  private static final int WHITE_LIST_BUTTON_ID = 1;
  private static final int BLACK_LIST_BUTTON_ID = 2;
  private static final int ROUND_ROBIN_BUTTON_ID = 3;

  public EmeraldPipeScreen(EmeraldPipeMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
    this.imageWidth = 175;
    this.imageHeight = 161;
    this.inventoryLabelY = this.imageHeight - 93;
  }

  @Override
  protected void initilaizeLedger(Inventory p_97742_) {
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight) / 2;
    guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
    guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }
}
