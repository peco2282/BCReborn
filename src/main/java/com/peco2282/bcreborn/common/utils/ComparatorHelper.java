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
package com.peco2282.bcreborn.common.utils;

import java.util.function.IntPredicate;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/** Original {@code BlockBuildCraft.getComparatorInputOverride()} for {@code IComparatorInventory} blocks. */
public final class ComparatorHelper {
  private ComparatorHelper() {
  }

  /**
   * The average fullness of the slots {@code counted} accepts, as 0..15: {@code floor(fullness * 14)}, plus one when
   * any counted slot holds something.
   */
  public static int fromContainer(Container container, IntPredicate counted) {
    int count = 0;
    int nonEmpty = 0;
    float power = 0;
    for (int slot = 0; slot < container.getContainerSize(); slot++) {
      if (!counted.test(slot)) continue;
      count++;
      ItemStack stack = container.getItem(slot);
      if (!stack.isEmpty()) {
        nonEmpty++;
        power += (float) stack.getCount() / Math.min(container.getMaxStackSize(), stack.getMaxStackSize());
      }
    }
    // The original divides by zero here; no counted slot gives no signal.
    if (count == 0) return 0;
    return Mth.floor(power / count * 14.0F) + (nonEmpty > 0 ? 1 : 0);
  }
}
