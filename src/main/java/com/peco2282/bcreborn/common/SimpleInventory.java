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
package com.peco2282.bcreborn.common;

import com.peco2282.bcreborn.api.core.INBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Arrays;
import java.util.LinkedList;

public class SimpleInventory implements Container, INBT {
  private final ItemStack[] contents;
  private final String name;
  private final int stackLimit;
  private final LinkedList<BlockEntity> listener = new LinkedList<>();

  public SimpleInventory(int size, String invName, int invStackLimit) {
    contents = CodingUtils.apply(new ItemStack[size], s -> {
      for (int i = 0; i < size; i++) {
        s[i] = ItemStack.EMPTY;
      }
    });
    name = invName;
    stackLimit = invStackLimit;
  }

  public static SimpleInventory sized(int size, String invName, int invStackLimit) {
    return new SimpleInventory(size, invName, invStackLimit);
  }

  @Override
  public void read(CompoundTag nbt) {
    if (nbt.contains("items")) {
      // this is to support legacy item load, the new format should be
      // "Items"
      readFromNBT(nbt, "items");
    } else {
      readFromNBT(nbt, "Items");
    }
  }


  public void readFromNBT(CompoundTag data, String tag) {
    ListTag nbttaglist = data.getList(tag, CompoundTag.TAG_COMPOUND);

    for (int j = 0; j < nbttaglist.size(); ++j) {
      CompoundTag slot = nbttaglist.getCompound(j);
      int index;
      if (slot.contains("index")) {
        index = slot.getInt("index");
      } else {
        index = slot.getByte("Slot");
      }
      if (index >= 0 && index < contents.length) {
        setItem(index, ItemStack.of(slot));
      }
    }
  }

  @Override
  public void write(CompoundTag nbt) {
    writeToNBT(nbt, "items");
  }

  public void writeToNBT(CompoundTag data, String tag) {
    ListTag slots = new ListTag();
    for (byte index = 0; index < contents.length; ++index) {
      if (contents[index] != null && contents[index].getCount() > 0) {
        CompoundTag slot = new CompoundTag();
        slots.add(slot);
        slot.putByte("Slot", index);
        contents[index].save(slot);
      }
    }
    data.put(tag, slots);
  }

  public void addListener(BlockEntity listner) {
    listener.add(listner);
  }

  @Override
  public int getContainerSize() {
    return contents.length;
  }

  @Override
  public boolean isEmpty() {
    return Arrays.stream(contents).noneMatch(s -> s == null || s.isEmpty());
  }

  @Override
  public ItemStack getItem(int i) {
    return contents[i];
  }

  @Override
  public ItemStack removeItem(int i, int i1) {
    if (i < contents.length && contents[i] != null) {
      if (contents[i].getCount() > i1) {
        return contents[i].split(i1);
      }
      if (contents[i].getCount() < i1) {
        return ItemStack.EMPTY;
      }
      ItemStack stack = contents[i];
      setItem(i, ItemStack.EMPTY);
      return stack;
    }
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeItemNoUpdate(int i) {
    if (i < contents.length) {
      ItemStack stack = contents[i];
      contents[i] = ItemStack.EMPTY;
      return stack;
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void setItem(int i, ItemStack itemStack) {
    if (i >= contents.length) {
      return;
    }
    contents[i] = itemStack;

    if (itemStack.getCount() > this.getMaxStackSize()) {
      itemStack.setCount(getMaxStackSize());
    }
  }

  @Override
  public void setChanged() {

  }

  @Override
  public boolean stillValid(Player player) {
    return true;
  }

  @Override
  public void clearContent() {

  }

  public String getName() {
    return name;
  }

  public int getStackLimit() {
    return stackLimit;
  }
}
