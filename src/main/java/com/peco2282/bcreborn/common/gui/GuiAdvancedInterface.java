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
package com.peco2282.bcreborn.common.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public abstract class GuiAdvancedInterface<M extends BuildCraftMenu<M>> extends BuildCraftScreen<M> {

  public ArrayList<AdvancedSlot> slots = new ArrayList<>();

  public GuiAdvancedInterface(M container, Inventory inventory, Component texture) {
    super(container, inventory, texture);
  }

  public int getSlotIndexAtLocation(double i, double j) {
    double x = i - leftPos;
    double y = j - topPos;

    for (int position = 0; position < slots.size(); ++position) {
      AdvancedSlot s = slots.get(position);

      if (s != null && x >= s.x && x <= s.x + 16 && y >= s.y && y <= s.y + 16) {
        return position;
      }
    }
    return -1;
  }

  public AdvancedSlot getSlotAtLocation(double i, double j) {
    int id = getSlotIndexAtLocation(i, j);

    if (id != -1) {
      return slots.get(id);
    } else {
      return null;
    }
  }

  private boolean isMouseOverSlot(AdvancedSlot slot, int mouseX, int mouseY) {
    int realMouseX = mouseX - this.leftPos;
    int realMouseY = mouseY - this.topPos;
    return realMouseX >= slot.x - 1 && realMouseX < slot.x + 16 + 1 && realMouseY >= slot.y - 1 && realMouseY < slot.y + 16 + 1;
  }

  protected void drawSlotHighlight(GuiGraphics guiGraphics, AdvancedSlot slot, int mouseX, int mouseY) {
    if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.shouldDrawHighlight()) {
      RenderSystem.disableDepthTest();
      guiGraphics.fillGradient(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, -2130706433, -2130706433);
      RenderSystem.enableDepthTest();
    }
  }

  protected void drawBackgroundSlots(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    if (slots != null) {
      for (AdvancedSlot slot : slots) {
        if (slot != null) {
          slot.drawSprite(guiGraphics, leftPos, topPos);
          drawSlotHighlight(guiGraphics, slot, mouseX, mouseY);
        }
      }
    }
  }

  public void drawTooltipForSlotAt(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    AdvancedSlot slot = getSlotAtLocation(mouseX, mouseY);

    if (slot != null) {
      slot.drawTooltip(this, guiGraphics, mouseX, mouseY);
    }
  }

  public void drawTooltip(GuiGraphics guiGraphics, String caption, int mouseX, int mouseY) {
    if (caption != null && !caption.isEmpty()) {
      guiGraphics.renderTooltip(this.font, Component.literal(caption), mouseX, mouseY);
    }
  }

  public int getXSize() {
    return imageWidth;
  }

  public int getYSize() {
    return imageHeight;
  }

  public void renderTooltip(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
    guiGraphics.renderTooltip(this.font, stack, x, y);
  }

  public void drawStack(GuiGraphics guiGraphics, ItemStack item, int x, int y) {
    if (item != null && !item.isEmpty()) {
      guiGraphics.renderItem(item, x, y);
      guiGraphics.renderItemDecorations(this.font, item, x, y);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);

    AdvancedSlot slot = getSlotAtLocation((int) mouseX, (int) mouseY);

    if (slot != null && slot.isDefined()) {
      slotClicked(slot, mouseButton);
    }

    return result;
  }

  public void resetNullSlots(int size) {
    slots.clear();

    for (int i = 0; i < size; ++i) {
      slots.add(null);
    }
  }

  // TODO: Use this for all children of this class
  protected void slotClicked(AdvancedSlot slot, int mouseButton) {

  }
}
