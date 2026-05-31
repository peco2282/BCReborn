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

import com.peco2282.bcreborn.common.inventory.StackHelper;
import net.minecraft.world.item.ItemStack;

/**
 * Returns true if the stack matches any one one of the filter stacks.
 */
public class ArrayStackFilter implements IStackFilter {

  protected ItemStack[] stacks;

  public ArrayStackFilter(ItemStack... stacks) {
    this.stacks = stacks;
  }

  @Override
  public boolean matches(ItemStack stack) {
    if (stacks.length == 0 || !hasFilter()) {
      return true;
    }
    for (ItemStack s : stacks) {
      if (StackHelper.isMatchingItem(s, stack)) {
        return true;
      }
    }
    return false;
  }

  public boolean matches(IStackFilter filter2) {
    for (ItemStack s : stacks) {
      if (filter2.matches(s)) {
        return true;
      }
    }

    return false;
  }

  public ItemStack[] getStacks() {
    return stacks;
  }

  public boolean hasFilter() {
    for (ItemStack filter : stacks) {
      if (filter != null) {
        return true;
      }
    }
    return false;
  }
}
