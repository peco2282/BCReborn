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

/**
 * Returns true if the stack matches any one one of the filter stacks.
 */
public class CompositeFilter implements IStackFilter {

  private final IStackFilter[] filters;

  public CompositeFilter(IStackFilter... iFilters) {
    filters = iFilters;
  }

  @Override
  public boolean matches(ItemStack stack) {
    for (IStackFilter f : filters) {
      if (f.matches(stack)) {
        return true;
      }
    }

    return false;
  }
}
