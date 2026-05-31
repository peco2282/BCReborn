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
package com.peco2282.bcreborn.common.gui.tooltips;

import net.minecraft.network.chat.Component;

public class ToolTipLine {

  public final int color;
  public String text;
  public int spacing;

  public ToolTipLine(String text, int color) {
    this.text = text;
    this.color = color;
  }

  public ToolTipLine(String text) {
    this(text, -1);
  }

  public ToolTipLine() {
    this("", -1);
  }

  public int getSpacing() {
    return spacing;
  }

  public void setSpacing(int spacing) {
    this.spacing = spacing;
  }

  public Component getComponent() {
    return Component.literal(text).withStyle(style -> style.withColor(color));
  }

}
