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
package com.peco2282.bcreborn.common.gui.slots;


import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotHidden extends Slot {

  private final int saveX;
  private final int saveY;

  public SlotHidden(Container inv, int index, int x, int y) {
    super(inv, index, x, y);

    saveX = x;
    saveY = y;
  }

  public void show() {
    x = saveX;
    y = saveY;
  }

  public void hide() {
    x = 9999;
    y = 9999;
  }
}