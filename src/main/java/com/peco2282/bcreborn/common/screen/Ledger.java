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

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code GuiBuildCraft.Ledger}: a tab on the right of a machine GUI that slides open on click
 * (24 x 24 closed, up to {@link #maxWidth} x {@link #maxHeight} open) to show machine information.
 */
@OnlyIn(Dist.CLIENT)
public abstract class Ledger {
  /** Original {@code SessionVars.openedLedger}: the ledger type stays open across GUIs. */
  @Nullable
  private static Class<? extends Ledger> openedLedger;

  public int currentShiftX = 0;
  public int currentShiftY = 0;
  protected int overlayColor = 0xffffff;
  protected int maxWidth = 124;
  protected int minWidth = 24;
  protected int currentWidth = minWidth;
  protected int maxHeight = 24;
  protected int minHeight = 24;
  protected int currentHeight = minHeight;
  private boolean open;
  private long lastUpdateTime = -1;

  /** Original {@code update()}: grows or shrinks by one pixel per 8 ms. */
  public void update() {
    long now = System.currentTimeMillis();
    if (lastUpdateTime < 0) {
      lastUpdateTime = now;
    }
    int step = (int) Math.round((now - lastUpdateTime) / 8.0);
    if (open && currentWidth < maxWidth) {
      currentWidth = Math.min(maxWidth, currentWidth + step);
    } else if (!open && currentWidth > minWidth) {
      currentWidth = Math.max(minWidth, currentWidth - step);
    }
    if (open && currentHeight < maxHeight) {
      currentHeight = Math.min(maxHeight, currentHeight + step);
    } else if (!open && currentHeight > minHeight) {
      currentHeight = Math.max(minHeight, currentHeight - step);
    }
    lastUpdateTime = now;
  }

  public int getHeight() {
    return currentHeight;
  }

  /** Draws the ledger with its top-left corner at the absolute screen position ({@code x}, {@code y}). */
  public abstract void draw(GuiGraphics graphics, Font font, int x, int y);

  public abstract String getTooltip();

  public boolean handleMouseClicked(double x, double y, int mouseButton) {
    return false;
  }

  public boolean intersectsWith(double mouseX, double mouseY, int shiftX, int shiftY) {
    return mouseX >= shiftX && mouseX <= shiftX + currentWidth && mouseY >= shiftY && mouseY <= shiftY + getHeight();
  }

  public void setFullyOpen() {
    open = true;
    currentWidth = maxWidth;
    currentHeight = maxHeight;
  }

  public void toggleOpen() {
    open = !open;
    openedLedger = open ? getClass() : null;
  }

  static boolean wasOpen(Ledger ledger) {
    return openedLedger != null && openedLedger == ledger.getClass();
  }

  public boolean isVisible() {
    return true;
  }

  public boolean isOpen() {
    return open;
  }

  protected boolean isFullyOpened() {
    return currentWidth >= maxWidth;
  }

  /** Original {@code drawBackground()}: the four edges and the body from ledger.png, tinted with the overlay colour. */
  protected void drawBackground(GuiGraphics graphics, int x, int y) {
    float r = ((overlayColor >> 16) & 0xff) / 255F;
    float g = ((overlayColor >> 8) & 0xff) / 255F;
    float b = (overlayColor & 0xff) / 255F;
    ResourceLocation texture = BuildCraftScreen.LEDGER_TEXTURE;
    graphics.setColor(r, g, b, 1F);
    graphics.blit(texture, x, y, 0, 256 - currentHeight, 4, currentHeight);
    graphics.blit(texture, x + 4, y, 256 - currentWidth + 4, 0, currentWidth - 4, 4);
    graphics.blit(texture, x, y, 0, 0, 4, 4);
    graphics.blit(texture, x + 4, y + 4, 256 - currentWidth + 4, 256 - currentHeight + 4, currentWidth - 4, currentHeight - 4);
    graphics.setColor(1F, 1F, 1F, 1F);
  }

  /** Draws a standalone 16 x 16 icon texture. */
  protected void drawIcon(GuiGraphics graphics, ResourceLocation icon, int x, int y) {
    graphics.blit(icon, x, y, 0, 0, 16, 16, 16, 16);
  }
}
