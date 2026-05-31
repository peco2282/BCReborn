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
package com.peco2282.bcreborn.robotics.screen;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.robotics.menu.RequesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RequesterScreen extends BuildCraftScreen<RequesterMenu> {

  private static final ResourceLocation TEXTURE = BCReborn.getLocation("textures/gui/requester_gui.png");

  public RequesterScreen(RequesterMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.imageWidth = 196;
    this.imageHeight = 181;
    menu.gui = this;
    menu.getRequestList();
  }

  @Override
  protected ResourceLocation getMenuTexture() {
    return TEXTURE;
  }

  @Override
  protected void initilaizeLedger(Inventory p_97742_) {
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(graphics, mouseX, mouseY);
  }
}