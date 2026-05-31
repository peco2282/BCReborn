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
import net.minecraft.world.entity.player.Player;

public class SlotPhantom extends SlotBase implements IPhantomSlot {

  public SlotPhantom(Container iinventory, int slotIndex, int posX, int posY) {
    super(iinventory, slotIndex, posX, posY);
  }

  @Override
  public boolean canAdjust() {
    return true;
  }

  @Override
  public boolean mayPickup(Player par1EntityPlayer) {
    return false;
  }
}
