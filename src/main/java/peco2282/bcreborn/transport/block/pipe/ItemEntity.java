/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class ItemEntity implements Entity {
  private ItemStack stack;
  public static final ItemEntity EMPTY = new ItemEntity(ItemStack.EMPTY);

  public static ItemEntity of(ItemStack stack) {
    return new ItemEntity(stack);
  }

  private ItemEntity(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public CompoundTag serialize(HolderLookup.Provider registryAccess) {
    return (CompoundTag) stack.save(registryAccess, new CompoundTag());
  }

  @Override
  public void deserialize(HolderLookup.Provider registryAccess, CompoundTag tag) {
    stack = ItemStack.parseOptional(registryAccess, tag);
  }
}
