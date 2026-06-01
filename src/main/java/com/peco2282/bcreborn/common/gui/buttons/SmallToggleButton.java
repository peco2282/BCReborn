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
package com.peco2282.bcreborn.common.gui.buttons;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmallToggleButton extends ToggleButton {

  public SmallToggleButton(int i, int j, int k, String s, boolean active) {
    this(i, j, k, 200, s, active);
  }

  public SmallToggleButton(int i, int x, int y, int w, String s, boolean active) {
    super(i, x, y, w, StandardButtonTextureSets.SMALL_BUTTON, s, active);
    this.active = active;
  }
}
