package com.peco2282.bcreborn.common.gui;

import net.minecraft.client.gui.components.Tooltip;

public interface ITooltipProvider {
  Tooltip getTooltip();

  boolean isTooltipVisible();

  boolean isMouseOver(double mouseX, double mouseY);
}
