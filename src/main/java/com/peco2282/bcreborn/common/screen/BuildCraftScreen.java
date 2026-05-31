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

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.gui.widgets.Widget;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class BuildCraftScreen<M extends BuildCraftMenu<M>> extends AbstractContainerScreen<M> {
  public static final ResourceLocation LEDGER_TEXTURE = ResourceLocation.fromNamespaceAndPath(BCRebornCore.MODID, "textures/gui/ledger.png");

  protected final LedgerManager ledgerManager = new LedgerManager(this);

  public BuildCraftScreen(M p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
    this.initilaizeLedger(p_97742_);
  }

  protected abstract void initilaizeLedger(Inventory p_97742_);

  public void drawTexturedModalRect(GuiGraphics guiGraphics, int x, int y, int u, int v, int width, int height) {
    ResourceLocation texture = getMenuTexture();
    if (texture != null)
      guiGraphics.blit(texture, x, y, u, v, width, height);
  }

  @Override
  protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
  }

  protected void drawWidgets(GuiGraphics guiGraphics, int mX, int mY) {
    for (Widget widget : menu.getWidgets()) {
      if (widget.hidden) continue;
      widget.draw(guiGraphics, this, getGuiLeft(), getGuiTop(), mX, mY);
    }
  }

  protected ResourceLocation getMenuTexture() {
    return null;
  }

  public int getXSize() {
    return this.imageWidth;
  }

  public int getYSize() {
    return this.imageHeight;
  }
}
