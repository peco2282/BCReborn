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


import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;

public enum LockButtonState implements IMultiButtonState {

  UNLOCKED(new ButtonTextureSet(224, 0, 16, 16)), LOCKED(new ButtonTextureSet(240, 0, 16, 16));
  public static final LockButtonState[] VALUES = values();
  private final IButtonTextureSet texture;

  LockButtonState(IButtonTextureSet texture) {
    this.texture = texture;
  }

  @Override
  public String getLabel() {
    return "";
  }

  @Override
  public IButtonTextureSet getTextureSet() {
    return texture;
  }

  @Override
  public ToolTip getToolTip() {
    return null;
  }
}
