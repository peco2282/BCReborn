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

public class SlotLimited extends SlotBase {

  private final int limit;

  public SlotLimited(Container iinventory, int slotIndex, int posX, int posY, int limit) {
    super(iinventory, slotIndex, posX, posY);
    this.limit = limit;
  }

  @Override
  public int getMaxStackSize() {
    return limit;
  }


}
