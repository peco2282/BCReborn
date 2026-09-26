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

import java.util.function.IntPredicate;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

/** Automation may access only real input/output slots, never recipe ghosts. */
public class MachineItemHandler extends InvWrapper {
  private final IntPredicate input;
  private final IntPredicate output;

  public MachineItemHandler(Container container, IntPredicate input, IntPredicate output) {
    super(container);
    this.input = input;
    this.output = output;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    return input.test(slot) || output.test(slot) ? super.getStackInSlot(slot) : ItemStack.EMPTY;
  }

  @Override
  public boolean isItemValid(int slot, ItemStack stack) {
    return input.test(slot) && super.isItemValid(slot, stack);
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    return output.test(slot) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
  }
}

