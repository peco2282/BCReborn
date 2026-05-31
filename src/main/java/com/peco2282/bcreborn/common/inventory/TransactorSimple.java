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
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TransactorSimple extends Transactor {

  protected Container inventory;

  public TransactorSimple(Container inventory) {
    this.inventory = inventory;
  }

  @Override
  public int inject(ItemStack stack, Direction orientation, boolean doAdd) {
    List<IInvSlot> filledSlots = new ArrayList<>(inventory.getContainerSize());
    List<IInvSlot> emptySlots = new ArrayList<>(inventory.getContainerSize());
    for (IInvSlot slot : InventoryIterator.getIterable(inventory, orientation)) {
      if (slot.canPutStackInSlot(stack)) {
        if (slot.getStackInSlot().isEmpty()) {
          emptySlots.add(slot);
        } else {
          filledSlots.add(slot);
        }
      }
    }

    int injected = 0;
    injected = tryPut(stack, filledSlots, injected, doAdd);
    injected = tryPut(stack, emptySlots, injected, doAdd);

    if (injected > 0 && doAdd) {
      inventory.setChanged();
    }
    return injected;
  }

  private int tryPut(ItemStack stack, List<IInvSlot> slots, int injected, boolean doAdd) {
    int realInjected = injected;

    if (realInjected >= stack.getCount()) {
      return realInjected;
    }

    for (IInvSlot slot : slots) {
      ItemStack stackInSlot = slot.getStackInSlot();
      if (stackInSlot.isEmpty() || StackHelper.canStacksMerge(stackInSlot, stack)) {
        int used = addToSlot(slot, stack, realInjected, doAdd);
        if (used > 0) {
          realInjected += used;
          if (realInjected >= stack.getCount()) {
            return realInjected;
          }
        }
      }
    }

    return realInjected;
  }

  /**
   *
   * @param slot
   * @param stack
   * @param injected Amount not to move?
   * @param doAdd
   * @return Return the number of items moved.
   */
  protected int addToSlot(IInvSlot slot, ItemStack stack, int injected, boolean doAdd) {
    int available = stack.getCount() - injected;
    int max = Math.min(stack.getMaxStackSize(), inventory.getMaxStackSize());

    ItemStack stackInSlot = slot.getStackInSlot();
    if (stackInSlot.isEmpty()) {
      int wanted = Math.min(available, max);
      if (doAdd) {
        stackInSlot = stack.copy();
        stackInSlot.setCount(wanted);
        slot.setStackInSlot(stackInSlot);
      }
      return wanted;
    }

    if (!StackHelper.canStacksMerge(stack, stackInSlot)) {
      return 0;
    }

    int wanted = max - stackInSlot.getCount();
    if (wanted <= 0) {
      return 0;
    }

    if (wanted > available) {
      wanted = available;
    }

    if (doAdd) {
      stackInSlot.grow(wanted);
      slot.setStackInSlot(stackInSlot);
    }
    return wanted;
  }

  @Override
  public ItemStack remove(IStackFilter filter, Direction orientation, boolean doRemove) {
    for (IInvSlot slot : InventoryIterator.getIterable(inventory, orientation)) {
      ItemStack stack = slot.getStackInSlot();
      if (!stack.isEmpty() && slot.canTakeStackFromSlot(stack) && filter.matches(stack)) {
        if (doRemove) {
          return slot.decreaseStackInSlot(1);
        } else {
          ItemStack output = stack.copy();
          output.setCount(1);
          return output;
        }
      }
    }
    return ItemStack.EMPTY;
  }
}
