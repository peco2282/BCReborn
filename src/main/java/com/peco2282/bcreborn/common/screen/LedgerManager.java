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
package com.peco2282.bcreborn.common.screen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code GuiBuildCraft.LedgerManager}: stacks the ledgers down the right edge of the GUI,
 * draws them with a tooltip on hover, and opens the clicked one (closing the others).
 */
@OnlyIn(Dist.CLIENT)
public class LedgerManager {
  private final BuildCraftScreen<?> screen;
  private final List<Ledger> ledgers = new ArrayList<>();

  public LedgerManager(BuildCraftScreen<?> screen) {
    this.screen = screen;
  }

  public void add(Ledger ledger) {
    ledgers.add(ledger);
    if (Ledger.wasOpen(ledger)) {
      ledger.setFullyOpen();
    }
  }

  public boolean isEmpty() {
    return ledgers.isEmpty();
  }

  @Nullable
  private Ledger getAtPosition(double mouseX, double mouseY) {
    int xShift = screen.getGuiLeft() + screen.getXSize();
    int yShift = screen.getGuiTop() + 8;
    for (Ledger ledger : ledgers) {
      if (!ledger.isVisible()) continue;
      ledger.currentShiftX = xShift;
      ledger.currentShiftY = yShift;
      if (ledger.intersectsWith(mouseX, mouseY, xShift, yShift)) {
        return ledger;
      }
      yShift += ledger.getHeight();
    }
    return null;
  }

  public void drawLedgers(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    int x = screen.getGuiLeft() + screen.getXSize();
    int y = screen.getGuiTop() + 8;
    for (Ledger ledger : ledgers) {
      ledger.update();
      if (!ledger.isVisible()) continue;
      ledger.draw(graphics, font, x, y);
      y += ledger.getHeight();
    }
    Ledger hovered = getAtPosition(mouseX, mouseY);
    if (hovered != null) {
      String tooltip = hovered.getTooltip();
      int startX = mouseX + 12;
      int startY = mouseY - 12;
      int textWidth = font.width(tooltip);
      graphics.fill(startX - 3, startY - 3, startX + textWidth + 3, startY + 8 + 3, 0xc0000000);
      graphics.drawString(font, tooltip, startX, startY, -1, true);
    }
  }

  /** @return true if a ledger took the click */
  public boolean handleMouseClicked(double x, double y, int mouseButton) {
    if (mouseButton != 0) return false;
    Ledger ledger = getAtPosition(x, y);
    if (ledger == null) return false;
    if (!ledger.handleMouseClicked(x, y, mouseButton)) {
      for (Ledger other : ledgers) {
        if (other != ledger && other.isOpen()) other.toggleOpen();
      }
      ledger.toggleOpen();
    }
    return true;
  }
}
