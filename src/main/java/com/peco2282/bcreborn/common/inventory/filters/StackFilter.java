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
package com.peco2282.bcreborn.common.inventory.filters;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

/**
 * This interface is used with several of the functions in IItemTransfer to
 * provide a convenient means of dealing with entire classes of items without
 * having to specify each item individually.
 */
public enum StackFilter implements IStackFilter {

  ALL {
    @Override
    public boolean matches(ItemStack stack) {
      return true;
    }
  },
  FUEL {
    @Override
    public boolean matches(ItemStack stack) {
      return ForgeHooks.getBurnTime(stack, null) > 0;
    }
  };

  @Override
  public abstract boolean matches(ItemStack stack);
}
