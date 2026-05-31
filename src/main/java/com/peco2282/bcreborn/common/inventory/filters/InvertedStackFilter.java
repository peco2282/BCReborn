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

public class InvertedStackFilter implements IStackFilter {

  private final IStackFilter filter;

  public InvertedStackFilter(IStackFilter filter) {
    this.filter = filter;
  }

  @Override
  public boolean matches(ItemStack stack) {
    if (stack == null) {
      return false;
    }
    return !filter.matches(stack);
  }
}
