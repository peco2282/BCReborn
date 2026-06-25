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
package com.peco2282.bcreborn.api;


import com.peco2282.bcreborn.api.items.IList;
import net.minecraft.world.item.ItemStack;

/**
 * Utility class for {@link ItemStack} operations, including merging and comparison.
 */
public class StackHelper {

  /**
   * Protected constructor to prevent instantiation from other packages.
   */
  protected StackHelper() {
  }

  /* STACK MERGING */

  /**
   * Checks if two ItemStacks (or {@link IList} items) can be merged.
   *
   * @param stack1 The first stack.
   * @param stack2 The second stack.
   * @return True if they can be merged.
   */
  public static boolean canStacksOrListsMerge(ItemStack stack1, ItemStack stack2) {
    if (stack1.isEmpty() || stack2.isEmpty()) {
      return false;
    }

    if (stack1.getItem() instanceof IList list) {
      return list.matches(stack1, stack2);
    } else if (stack2.getItem() instanceof IList list) {
      return list.matches(stack2, stack1);
    }

    if (!ItemStack.isSameItem(stack1, stack2)) {
      return false;
    }
    return ItemStack.isSameItemSameTags(stack1, stack2);

  }

  /**
   * Merges the source stack into the target stack.
   *
   * @param mergeSource The stack to merge from. This stack is not modified.
   * @param mergeTarget The stack to merge into. This stack is modified if {@code doMerge} is true.
   * @param doMerge     Whether to perform the actual merge.
   * @return The number of items that were successfully merged.
   */
  public static int mergeStacks(ItemStack mergeSource, ItemStack mergeTarget, boolean doMerge) {
    if (!ItemStack.isSameItemSameTags(mergeSource, mergeTarget)) {
      return 0;
    }
    int mergeCount = Math.min(mergeTarget.getMaxStackSize() - mergeTarget.getCount(), mergeSource.getCount());
    if (mergeCount < 1) {
      return 0;
    }
    if (doMerge) {
      mergeTarget.setCount(mergeTarget.getCount() + mergeCount);
    }
    return mergeCount;
  }

  /* ITEM COMPARISONS */

  /**
   * Determines whether the given ItemStack should be considered equivalent for crafting purposes.
   *
   * @param base       The stack to compare to.
   * @param comparison The stack to compare.
   * @return True if they are considered equivalent.
   */
  public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison) {
    return isMatchingItem(base, comparison, true, false);
  }

  /**
   * Checks if two stacks (or lists) match.
   *
   * @param a The first stack.
   * @param b The second stack.
   * @return True if they match.
   */
  public static boolean isMatchingItemOrList(final ItemStack a, final ItemStack b) {
    if (a.isEmpty() || b.isEmpty()) {
      return a.isEmpty() && b.isEmpty();
    }

    return isMatchingItem(a, b, true, false);
  }

  /**
   * Compares item, damage, and NBT.
   *
   * @param base       The stack to compare to.
   * @param comparison The stack to compare.
   * @return True if they match.
   */
  public static boolean isMatchingItem(final ItemStack base, final ItemStack comparison) {
    return isMatchingItem(base, comparison, true, true);
  }

  /**
   * Variant that checks damage for damaged items.
   *
   * @param a The first stack.
   * @param b The second stack.
   * @return True if they are equal.
   */
  public static boolean isEqualItem(final ItemStack a, final ItemStack b) {
    return isMatchingItem(a, b, false, true);
  }

  /**
   * Compares ItemStacks with optional damage and NBT matching.
   *
   * @param a           First ItemStack.
   * @param b           Second ItemStack.
   * @param matchDamage Whether to match damage.
   * @param matchNBT    Whether to match NBT.
   * @return True if they match.
   */
  public static boolean isMatchingItem(final ItemStack a, final ItemStack b, final boolean matchDamage,
                                       final boolean matchNBT) {
    if (a.isEmpty() || b.isEmpty()) {
      return a.isEmpty() && b.isEmpty();
    }

    if (a.getItem() != b.getItem()) {
      return false;
    }

    if (matchNBT) {
      return ItemStack.isSameItemSameTags(a, b);
    }

    return true;
  }
}
