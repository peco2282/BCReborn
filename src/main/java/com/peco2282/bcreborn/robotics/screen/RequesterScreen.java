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

import com.peco2282.bcreborn.BCRebornRobotics;
import net.minecraft.world.item.ItemStack;
import com.peco2282.bcreborn.robotics.block.entity.RequesterBlockEntity;
import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.robotics.menu.RequesterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RequesterScreen extends BuildCraftScreen<RequesterMenu> {

  private static final ResourceLocation TEXTURE = BCRebornRobotics.location("textures/gui/requester_gui.png");

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

  /** Original GuiRequester: the requests are 4 columns of 5 slots on the left. */
  private static int requestX(int index) {
    return 9 + (index / 5) * 18;
  }

  private static int requestY(int index) {
    return 7 + (index % 5) * 18;
  }

  private int requestAt(double mouseX, double mouseY) {
    for (int i = 0; i < RequesterBlockEntity.NB_ITEMS; i++) {
      double x = mouseX - leftPos - requestX(i);
      double y = mouseY - topPos - requestY(i);
      if (x >= 0 && x < 16 && y >= 0 && y < 16) return i;
    }
    return -1;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    int index = requestAt(mouseX, mouseY);
    if (index >= 0 && minecraft != null && minecraft.gameMode != null) {
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    for (int i = 0; i < RequesterBlockEntity.NB_ITEMS; i++) {
      ItemStack request = menu.requests[i];
      int x = leftPos + requestX(i);
      int y = topPos + requestY(i);
      if (request != null && !request.isEmpty()) {
        graphics.renderItem(request, x, y);
        graphics.renderItemDecorations(font, request, x, y);
      }
      if (requestAt(mouseX, mouseY) == i) {
        graphics.fill(x, y, x + 16, y + 16, 0x80FFFFFF);
      }
    }
  }

  /** Original GuiRequester draws no title: the request grid starts at the top edge. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(graphics, mouseX, mouseY);
    int index = requestAt(mouseX, mouseY);
    if (index >= 0 && menu.requests[index] != null && !menu.requests[index].isEmpty() && menu.getCarried().isEmpty()) {
      graphics.renderTooltip(font, menu.requests[index], mouseX, mouseY);
    }
  }
}