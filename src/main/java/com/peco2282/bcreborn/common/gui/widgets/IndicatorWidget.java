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
package com.peco2282.bcreborn.common.gui.widgets;


import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IndicatorWidget extends Widget {

  public final IIndicatorController controller;

  public IndicatorWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h) {
    super(x, y, u, v, w, h);
    this.controller = controller;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void draw(GuiGraphics guiGraphics, BuildCraftScreen<?> gui, int guiX, int guiY, int mouseX, int mouseY) {
    int scale = controller.getScaledLevel(h);
    gui.drawTexturedModalRect(guiGraphics, guiX + x, guiY + y + h - scale, u, v + h - scale, w, scale);
  }

  @Override
  public ToolTip getToolTip() {
    return controller.getToolTip();
  }

}
