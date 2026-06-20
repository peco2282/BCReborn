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
package com.peco2282.bcreborn.factory.screen;

import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.factory.menu.AutoWorkbenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AutoCraftingScreen extends BuildCraftScreen<AutoWorkbenchMenu> {
  public static final ResourceLocation TEXTURE = BCRebornFactory.location("textures/gui/autobench.png");

  public AutoCraftingScreen(AutoWorkbenchMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
    this.imageWidth = 176;
    this.imageHeight = 197;
    this.inventoryLabelY = this.imageHeight - 94;
  }

  @Override
  protected void initilaizeLedger(Inventory p_97742_) {
  }

  @Override
  protected ResourceLocation getMenuTexture() {
    return TEXTURE;
  }

  @Override
  protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    p_283065_.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

    int progress = menu.getProgress();
    if (progress > 0) {
      int scaledProgress = progress * 23 / 256; // AutoWorkbenchBlockEntity.CRAFT_TIME = 256
      p_283065_.blit(TEXTURE, x + 89, y + 45, 176, 0, scaledProgress + 1, 12);
    }
  }
}
