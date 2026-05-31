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

public abstract class IndicatorController implements IIndicatorController {

  private final ToolTip tips = new ToolTip() {
    @Override
    public void refresh() {
      refreshToolTip();
    }
  };
  protected ToolTipLine tip = new ToolTipLine();

  public IndicatorController() {
    tips.add(tip);
  }

  protected void refreshToolTip() {
  }

  @Override
  public final ToolTip getToolTip() {
    return tips;
  }
}
