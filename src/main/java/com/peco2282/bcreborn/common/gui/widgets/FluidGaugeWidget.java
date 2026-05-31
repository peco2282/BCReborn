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
import com.peco2282.bcreborn.common.gui.tooltips.ToolTipLine;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.energy.fluids.Tank;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fluids.FluidStack;

public class FluidGaugeWidget extends Widget {

  public final Tank tank;

  public FluidGaugeWidget(Tank tank, int x, int y, int u, int v, int w, int h) {
    super(x, y, u, v, w, h);
    this.tank = tank;
  }

  @Override
  public ToolTip getToolTip() {
    return new ToolTip(new ToolTipLine(tank.getToolTip().getString()));
  }

  @Override
  public void draw(GuiGraphics guiGraphics, BuildCraftScreen<?> gui, int guiX, int guiY, int mouseX, int mouseY) {
    if (tank == null) {
      return;
    }
    FluidStack fluidStack = tank.getFluid();
    if (fluidStack == null || fluidStack.getAmount() <= 0 || fluidStack.getFluid() == null) {
      return;
    }

    // Simplified for now, complex fluid rendering needs specialized helper
    int scale = (int) (h * (Math.min(fluidStack.getAmount(), tank.getCapacity()) / (float) tank.getCapacity()));
    // gui.drawFluid(guiGraphics, guiX + x, guiY + y + h - scale, fluidStack, w, scale);

    gui.drawTexturedModalRect(guiGraphics, guiX + x, guiY + y, u, v, w, h);
  }

}
