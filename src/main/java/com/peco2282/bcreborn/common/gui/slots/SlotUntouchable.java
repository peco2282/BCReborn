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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotUntouchable extends SlotBase implements IPhantomSlot {

  public SlotUntouchable(Container contents, int id, int x, int y) {
    super(contents, id, x, y);
  }

  @Override
  public boolean mayPlace(ItemStack itemstack) {
    return false;
  }

  @Override
  public boolean mayPickup(Player par1EntityPlayer) {
    return false;
  }

  @Override
  public boolean canAdjust() {
    return false;
  }

  @Override
  public boolean canShift() {
    return false;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean isActive() {
    return false;
  }
}
