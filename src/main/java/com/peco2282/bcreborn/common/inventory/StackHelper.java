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
package com.peco2282.bcreborn.common.inventory;


import com.peco2282.bcreborn.api.items.IList;
import net.minecraft.world.item.ItemStack;

public class StackHelper {

	protected StackHelper() {
	}

	/* STACK MERGING */

	/**
	 * Checks if two ItemStacks are identical enough to be merged
	 *
	 * @param stack1 - The first stack
	 * @param stack2 - The second stack
	 * @return true if stacks can be merged, false otherwise
	 */
	public static boolean canStacksMerge(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null || stack2 == null) {
			return false;
		}
		if (!ItemStack.isSameItem(stack1, stack2)) {
			return false;
		}
		if (!ItemStack.isSameItemSameTags(stack1, stack2)) {
			return false;
		}
		return true;

	}

	public static boolean canStacksOrListsMerge(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null || stack2 == null) {
			return false;
		}

		if (stack1.getItem() instanceof IList) {
			IList list = (IList) stack1.getItem();
			return list.matches(stack1, stack2);
		} else if (stack2.getItem() instanceof IList) {
			IList list = (IList) stack2.getItem();
			return list.matches(stack2, stack1);
		}

		if (!ItemStack.isSameItem(stack1, stack2)) {
			return false;
		}
		if (!ItemStack.isSameItemSameTags(stack1, stack2)) {
			return false;
		}
		return true;

	}

	/**
	 * Merges mergeSource into mergeTarget
	 *
	 * @param mergeSource - The stack to merge into mergeTarget, this stack is
	 * not modified
	 * @param mergeTarget - The target merge, this stack is modified if doMerge
	 * is set
	 * @param doMerge - To actually do the merge
	 * @return The number of items that was successfully merged.
	 */
	public static int mergeStacks(ItemStack mergeSource, ItemStack mergeTarget, boolean doMerge) {
		if (!canStacksMerge(mergeSource, mergeTarget)) {
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
	 * Determines whether the given ItemStack should be considered equivalent
	 * for crafting purposes.
	 *
	 * @param base The stack to compare to.
	 * @param comparison The stack to compare.
	 * @param oreDictionary true to take the Forge OreDictionary into account.
	 * @return true if comparison should be considered a crafting equivalent for
	 * base.
	 */
	public static boolean isCraftingEquivalent(ItemStack base, ItemStack comparison, boolean oreDictionary) {
		if (isMatchingItem(base, comparison, true, false)) {
			return true;
		}

		return false;
	}

	public static boolean isCraftingEquivalent(int[] oreIDs, ItemStack comparison) {
		return false;
	}

	public static boolean isMatchingItemOrList(final ItemStack a, final ItemStack b) {
		if (a.isEmpty() || b.isEmpty()) {
			return a.isEmpty() && b.isEmpty();
		}

		return isMatchingItem(a, b, true, false);
	}

	/**
	 * Compares item id, damage and NBT. Accepts wildcard damage. Ignores damage
	 * entirely if the item doesn't have subtypes.
	 *
	 * @param base The stack to compare to.
	 * @param comparison The stack to compare.
	 * @return true if id, damage and NBT match.
	 */
	public static boolean isMatchingItem(final ItemStack base, final ItemStack comparison) {
		return isMatchingItem(base, comparison, true, true);
	}

	/**
	 * This variant also checks damage for damaged items.
	 */
	public static boolean isEqualItem(final ItemStack a, final ItemStack b) {
		return isMatchingItem(a, b, false, true);
	}

	/**
	 * Compares item id, and optionally damage and NBT. Accepts wildcard damage.
	 * Ignores damage entirely if the item doesn't have subtypes.
	 *
	 * @param a ItemStack
	 * @param b ItemStack
	 * @param matchDamage
	 * @param matchNBT
	 * @return true if matches
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
			if (!ItemStack.isSameItemSameTags(a, b)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isWildcard(ItemStack stack) {
		return false;
	}

	public static boolean isWildcard(int damage) {
		return damage == -1;
	}
}
