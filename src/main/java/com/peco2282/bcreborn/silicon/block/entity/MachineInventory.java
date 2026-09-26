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
package com.peco2282.bcreborn.silicon.block.entity;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/** Plans a complete craft on copies before committing any inventory or energy. */
final class MachineInventory {
  private MachineInventory() {}

  static ItemStack[] copy(Container container) {
    ItemStack[] items = new ItemStack[container.getContainerSize()];
    for (int i = 0; i < items.length; i++) items[i] = container.getItem(i).copy();
    return items;
  }

  static boolean insert(ItemStack[] items, int first, int end, ItemStack stack) {
    ItemStack remaining = stack.copy();
    for (int pass = 0; pass < 2; pass++) {
      for (int i = first; i < end && !remaining.isEmpty(); i++) {
        ItemStack target = items[i];
        if (pass == 0 && !target.isEmpty() && ItemStack.isSameItemSameTags(target, remaining)) {
          int count = Math.min(remaining.getCount(), Math.min(64, target.getMaxStackSize()) - target.getCount());
          if (count > 0) { target.grow(count); remaining.shrink(count); }
        } else if (pass == 1 && target.isEmpty()) {
          int count = Math.min(remaining.getCount(), Math.min(64, remaining.getMaxStackSize()));
          items[i] = remaining.copyWithCount(count);
          remaining.shrink(count);
        }
      }
    }
    return remaining.isEmpty();
  }

  static void commit(Container container, ItemStack[] items) {
    for (int i = 0; i < items.length; i++) container.setItem(i, items[i]);
    container.setChanged();
  }
}

