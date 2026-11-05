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

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.transport.menu.EmzuliPipeMenu;
import com.peco2282.bcreborn.transport.pipe.runtime.EmzuliState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.DyeColor;

/**
 * Port of the original {@code GuiEmzuliPipe} and the {@code PaintWidget}s of {@code ContainerEmzuliPipe}.
 * Each paint button shows the paint of its filter slot (a paintbrush icon, or the "no paint" icon).
 * Left click: next colour, right click: previous colour, middle click: no paint.
 */
public class EmzuliPipeScreen extends BuildCraftScreen<EmzuliPipeMenu> {
  private static final ResourceLocation TEXTURE = BCRebornTransport.location("textures/gui/pipe_emzuli.png");
  private static final int BUTTON_U = 176;
  private static final int BUTTON_SIZE = 20;

  public EmzuliPipeScreen(EmzuliPipeMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.imageWidth = 176;
    this.imageHeight = 166;
    this.inventoryLabelY = this.imageHeight - 93;
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
  }

  private static ResourceLocation paintbrush(DyeColor color) {
    return BCRebornCore.location("textures/item/paintbrush/" + color.getName() + ".png");
  }

  private int hoveredButton(double mouseX, double mouseY) {
    for (int slot = 0; slot < EmzuliState.SLOTS; slot++) {
      int bx = leftPos + EmzuliPipeMenu.PAINT_BUTTONS[slot][0];
      int by = topPos + EmzuliPipeMenu.PAINT_BUTTONS[slot][1];
      if (mouseX >= bx && mouseX < bx + BUTTON_SIZE && mouseY >= by && mouseY < by + BUTTON_SIZE) {
        return slot;
      }
    }
    return -1;
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    int hovered = hoveredButton(mouseX, mouseY);
    for (int slot = 0; slot < EmzuliState.SLOTS; slot++) {
      int bx = leftPos + EmzuliPipeMenu.PAINT_BUTTONS[slot][0];
      int by = topPos + EmzuliPipeMenu.PAINT_BUTTONS[slot][1];
      guiGraphics.blit(TEXTURE, bx, by, BUTTON_U, slot == hovered ? BUTTON_SIZE : 0, BUTTON_SIZE, BUTTON_SIZE);
      DyeColor color = menu.getSlotColor(slot);
      if (color != null) {
        guiGraphics.blit(paintbrush(color), bx + 2, by + 2, 0, 0, 16, 16, 16, 16);
      } else {
        guiGraphics.blit(TEXTURE, bx + 2, by + 2, BUTTON_U, BUTTON_SIZE * 2, 16, 16);
      }
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    int slot = hoveredButton(mouseX, mouseY);
    if (slot >= 0 && button >= EmzuliPipeMenu.BUTTON_LEFT && button <= EmzuliPipeMenu.BUTTON_MIDDLE
      && minecraft != null && minecraft.gameMode != null) {
      int id = EmzuliPipeMenu.buttonId(slot, button);
      menu.clickMenuButton(minecraft.player, id);
      minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    Component title = Component.translatable("screen.pipes.emzuli.title");
    guiGraphics.drawString(this.font, title, (this.imageWidth - this.font.width(title)) / 2, 6, 0x404040, false);
    guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
    int slot = hoveredButton(mouseX, mouseY);
    if (slot >= 0) {
      DyeColor color = menu.getSlotColor(slot);
      Component tip = color != null
        ? Component.translatable("screen.pipes.emzuli.paint", Component.translatable("color.minecraft." + color.getName()))
        : Component.translatable("screen.pipes.emzuli.nopaint");
      guiGraphics.renderTooltip(this.font, tip, mouseX, mouseY);
    }
  }
}
