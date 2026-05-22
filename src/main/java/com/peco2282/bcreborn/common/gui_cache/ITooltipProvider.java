package com.peco2282.bcreborn.common.gui_cache;


import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import net.minecraft.network.chat.Component;

public interface ITooltipProvider {
  ToolTip getTooltip();

  boolean isTooltipVisible();

  boolean isMouseOver(double mouseX, double mouseY);

  default Component getTooltipText() {
    return getTooltip().getTooltipComponent();
  }
}
