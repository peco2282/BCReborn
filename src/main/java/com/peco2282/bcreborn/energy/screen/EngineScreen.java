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

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class EngineScreen<M extends BuildCraftMenu<M>> extends BuildCraftScreen<M> {
  public EngineScreen(M p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
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
}
