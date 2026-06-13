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
package com.peco2282.bcreborn.core.list;


import com.peco2282.bcreborn.api.StackHelper;
import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.api.lists.ListRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public final class ListHandlerOld {
  private static final WeakHashMap<ItemStack, StackLine[]> LINE_CACHE = new WeakHashMap<>();

  private ListHandlerOld() {

  }

  public static void saveLine(ItemStack stack, StackLine line, int index) {
    CompoundTag nbt = stack.getOrCreateTag();

    nbt.putBoolean("written", true);

    CompoundTag lineNBT = new CompoundTag();
    line.writeToNBT(lineNBT);
    nbt.put("line[" + index + "]", lineNBT);
  }

  public static StackLine[] getLines(ItemStack stack) {
    if (LINE_CACHE.containsKey(stack)) {
      return LINE_CACHE.get(stack);
    }

    StackLine[] result = new StackLine[6];

    for (int i = 0; i < 6; ++i) {
      result[i] = new StackLine();
    }

    CompoundTag nbt = stack.getOrCreateTag();

    if (nbt.contains("written")) {
      for (int i = 0; i < 6; ++i) {
        result[i].readFromNBT(nbt.getCompound("line[" + i + "]"));
      }
    }

    LINE_CACHE.put(stack, result);

    return result;
  }

  public static boolean matches(ItemStack stackList, ItemStack item) {
    StackLine[] lines = getLines(stackList);

    if (lines != null) {
      for (StackLine line : lines) {
        if (line != null && line.matches(item)) {
          return true;
        }
      }
    }

    return false;
  }

  public static class StackLine {
    private final NonNullList<ItemStack> stacks = NonNullList.withSize(7, ItemStack.EMPTY);
    private final List<ItemStack> ores = new ArrayList<>();
    private final List<ItemStack> relatedItems = new ArrayList<>();
    public boolean oreWildcard = false;
    public boolean subitemsWildcard = false;
    public boolean isOre;

    public ItemStack getStack(int index) {
      if (index == 0 || (!oreWildcard && !subitemsWildcard)) {
        if (index < 7) {
          return stacks.get(index);
        } else {
          return ItemStack.EMPTY;
        }
      } else if (oreWildcard) {
        if (ores.size() > (index - 1)) {
          return ores.get(index - 1);
        } else {
          return ItemStack.EMPTY;
        }
      } else {
        if (relatedItems.size() > (index - 1)) {
          return relatedItems.get(index - 1);
        } else {
          return ItemStack.EMPTY;
        }
      }
    }

    public void setStack(int slot, ItemStack stack) {
      if (slot >= 0 && slot < 7) {
        if (!stack.isEmpty()) {
          ItemStack copy = stack.copy();
          copy.setCount(1);
          stacks.set(slot, copy);
        } else {
          stacks.set(slot, ItemStack.EMPTY);
        }
      }

      if (slot == 0) {
        relatedItems.clear();
        ores.clear();

        if (stacks.get(0).isEmpty()) {
          isOre = false;
        } else {
          // We use ListMatchHandler to determine if it's an ore or has related items.
          isOre = false;
          List<ListMatchHandler> handlers = ListRegistry.getHandlers();
          for (ListMatchHandler h : handlers) {
            if (h.isValidSource(ListMatchHandler.Type.MATERIAL, stacks.get(0))) {
              isOre = true;
            }
          }
          // For client preview, we will rely on getExamples logic if needed,
          // but BuildCraft 1.7.10 had an explicit setClientPreviewLists.
          // Since we are now using a more generic handler system, we will simplify.
        }
      }
    }

    public void writeToNBT(CompoundTag nbt) {
      nbt.putBoolean("ore", oreWildcard);
      nbt.putBoolean("sub", subitemsWildcard);

      for (int i = 0; i < 7; ++i) {
        if (!stacks.get(i).isEmpty()) {
          CompoundTag stackNBT = new CompoundTag();
          stacks.get(i).save(stackNBT);
          nbt.put("stacks[" + i + "]", stackNBT);
        }
      }
    }

    public void readFromNBT(CompoundTag nbt) {
      oreWildcard = nbt.getBoolean("ore");
      subitemsWildcard = nbt.getBoolean("sub");

      for (int i = 0; i < 7; ++i) {
        if (nbt.contains("stacks[" + i + "]")) {
          setStack(i, ItemStack.of(nbt.getCompound("stacks[" + i + "]")));
        }
      }
    }

    private boolean classMatch(Item base, Item matched) {
      if (base.getClass() == Item.class) {
        return base == matched;
      } else if (base.getClass() == matched.getClass()) {
        if (base instanceof BlockItem) {
          Block baseBlock = ((BlockItem) base).getBlock();
          Block matchedBlock = ((BlockItem) matched).getBlock();

          if (baseBlock.getClass() == Block.class) {
            return baseBlock == matchedBlock;
          } else {
            return baseBlock.equals(matchedBlock);
          }
        } else {
          return true;
        }
      } else {
        return false;
      }
    }

    public boolean matches(ItemStack item) {
      if (stacks.get(0).isEmpty()) {
        return false;
      }
      if (subitemsWildcard) {
        return classMatch(stacks.get(0).getItem(), item.getItem());
      } else if (oreWildcard) {
        List<ListMatchHandler> handlers = ListRegistry.getHandlers();
        for (ListMatchHandler h : handlers) {
          if (h.matches(ListMatchHandler.Type.MATERIAL, stacks.get(0), item, false)) {
            return true;
          }
        }
        return false;
      } else {
        for (ItemStack stack : stacks) {
          if (!stack.isEmpty() && StackHelper.isMatchingItem(stack, item, true, false)) {
            return true;
          }
        }

        return false;
      }
    }
  }
}
