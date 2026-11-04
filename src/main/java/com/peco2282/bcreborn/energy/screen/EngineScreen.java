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
package com.peco2282.bcreborn.energy.screen;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.GeneralConfig;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.common.screen.Ledger;
import com.peco2282.bcreborn.energy.menu.EngineMenuInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Port of the original {@code GuiEngine}: adds the energy ledger (current output, stored energy, heat) unless
 * power values are hidden in the config.
 */
public abstract class EngineScreen<M extends BuildCraftMenu<M> & EngineMenuInfo> extends BuildCraftScreen<M> {
  private static final ResourceLocation ENERGY_ICON = BCRebornCore.location("textures/item/icons/energy.png");

  public EngineScreen(M p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
    if (!GeneralConfig.isHidePowerValues()) {
      ledgerManager.add(new EngineLedger(menu));
    }
  }

  /** Original {@code GuiEngine.EngineLedger}. */
  protected static class EngineLedger extends Ledger {
    private static final int HEADER = 0xe1c92f;
    private static final int SUBHEADER = 0xaaafb8;
    private static final int TEXT = 0x000000;
    private final EngineMenuInfo info;

    EngineLedger(EngineMenuInfo info) {
      this.info = info;
      maxHeight = 94;
      overlayColor = 0xd46c1f;
    }

    @Override
    public void draw(GuiGraphics graphics, Font font, int x, int y) {
      drawBackground(graphics, x, y);
      drawIcon(graphics, ENERGY_ICON, x + 3, y + 4);
      if (!isFullyOpened()) {
        return;
      }
      graphics.drawString(font, Component.translatable("gui.energy"), x + 22, y + 8, HEADER, true);
      graphics.drawString(font, Component.translatable("gui.currentOutput").append(":"), x + 22, y + 20, SUBHEADER, true);
      graphics.drawString(font, String.format("%d RF/t", info.getCurrentOutput()), x + 22, y + 32, TEXT, false);
      graphics.drawString(font, Component.translatable("gui.stored").append(":"), x + 22, y + 44, SUBHEADER, true);
      graphics.drawString(font, String.format("%d RF", info.getEnergy()), x + 22, y + 56, TEXT, false);
      graphics.drawString(font, Component.translatable("gui.heat").append(":"), x + 22, y + 68, SUBHEADER, true);
      graphics.drawString(font, String.format("%.2f °C", (float) info.getHeat()), x + 22, y + 80, TEXT, false);
    }

    @Override
    public String getTooltip() {
      return String.format("%d RF/t", info.getCurrentOutput());
    }
  }

  public void drawCenteredString(GuiGraphics guiGraphics, String string, int xCenter, int yCenter, int textColor) {
    guiGraphics.drawString(font, string, xCenter - font.width(string) / 2, yCenter - font.lineHeight / 2, textColor);
  }

  protected int getCenteredOffset(String string) {
    return getCenteredOffset(string, getXSize());
  }

  protected int getCenteredOffset(String string, int xWidth) {
    return (xWidth - font.width(string)) / 2;
  }

  /** Original {@code drawGuiContainerForegroundLayer()}: centred title and the inventory label. */
  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    graphics.drawString(font, title, (imageWidth - font.width(title)) / 2, 6, 0x404040, false);
    graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040, false);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTicks);
    renderTooltip(graphics, mouseX, mouseY);
  }
}
