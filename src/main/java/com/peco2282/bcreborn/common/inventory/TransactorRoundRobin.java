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

import com.peco2282.bcreborn.api.core.IInvSlot;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class TransactorRoundRobin extends TransactorSimple {

  public TransactorRoundRobin(Container inventory) {
    super(inventory);
  }

  @Override
  public int inject(ItemStack stack, Direction orientation, boolean doAdd) {

    int added = 0;

    for (int itemLoop = 0; itemLoop < stack.getCount(); ++itemLoop) { // add 1 item n times.

      int smallestStackSize = Integer.MAX_VALUE;
      IInvSlot minSlot = null;

      for (IInvSlot slot : InventoryIterator.getIterable(inventory, orientation)) {
        ItemStack stackInInventory = slot.getStackInSlot();

        if (stackInInventory.isEmpty()) {
          continue;
        }

        if (stackInInventory.getCount() >= stackInInventory.getMaxStackSize()) {
          continue;
        }

        if (stackInInventory.getCount() >= inventory.getMaxStackSize()) {
          continue;
        }

        if (ItemStack.isSameItemSameTags(stack, stackInInventory) && stackInInventory.getCount() < smallestStackSize) {
          smallestStackSize = stackInInventory.getCount();
          minSlot = slot;
        }
        if (smallestStackSize <= 1) {
          break;
        }
      }

      if (minSlot != null) {
        added += addToSlot(minSlot, stack, stack.getCount() - 1, doAdd); // add 1 item n times, into the selected slot
      } else {
        break;
      }

    }

    return added;
  }
}
