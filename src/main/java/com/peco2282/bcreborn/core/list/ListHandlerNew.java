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

import com.peco2282.bcreborn.api.lists.ListMatchHandler;
import com.peco2282.bcreborn.api.lists.ListRegistry;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListHandlerNew {
  public static final int WIDTH = 9;
  public static final int HEIGHT = 2;

  private ListHandlerNew() {

  }

  public static Line[] getLines(ItemStack item) {
    CompoundTag data = item.getOrCreateTag();
    if (data.contains("written") && data.contains("lines")) {
      ListTag list = data.getList("lines", 10);
      Line[] lines = new Line[list.size()];
      for (int i = 0; i < lines.length; i++) {
        lines[i] = Line.fromNBT(list.getCompound(i));
      }
      return lines;
    } else {
      Line[] lines = new Line[HEIGHT];
      for (int i = 0; i < lines.length; i++) {
        lines[i] = new Line();
      }
      return lines;
    }
  }

  public static void saveLines(ItemStack stackList, Line[] lines) {
    CompoundTag data = stackList.getOrCreateTag();
    data.putBoolean("written", true);
    ListTag lineList = new ListTag();
    for (Line l : lines) {
      lineList.add(l.toNBT());
    }
    data.put("lines", lineList);
  }

  public static boolean matches(ItemStack stackList, ItemStack item) {
    CompoundTag data = stackList.getOrCreateTag();
    if (data.contains("written") && data.contains("lines")) {
      ListTag list = data.getList("lines", 10);
      for (int i = 0; i < list.size(); i++) {
        Line line = Line.fromNBT(list.getCompound(i));
        if (line.matches(item)) {
          return true;
        }
      }
    }

    return false;
  }

  public static class Line {
    public final NonNullList<ItemStack> stacks;
    public boolean precise, byType, byMaterial;

    public Line() {
      stacks = NonNullList.withSize(WIDTH, ItemStack.EMPTY);
    }

    public static Line fromNBT(CompoundTag data) {
      Line line = new Line();

      if (data != null && data.contains("st")) {
        ListTag l = data.getList("st", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < l.size() && i < WIDTH; i++) {
          line.stacks.set(i, ItemStack.of(l.getCompound(i)));
        }

        line.precise = data.getBoolean("Fp");
        line.byType = data.getBoolean("Ft");
        line.byMaterial = data.getBoolean("Fm");
      }

      return line;
    }

    public boolean isOneStackMode() {
      return byType || byMaterial;
    }

    public boolean getOption(int id) {
      return id == 0 ? precise : (id == 1 ? byType : byMaterial);
    }

    public void toggleOption(int id) {
      if (!byType && !byMaterial && (id == 1 || id == 2)) {
        for (int i = 1; i < stacks.size(); i++) {
          stacks.set(i, ItemStack.EMPTY);
        }
      }
      switch (id) {
        case 0:
          precise = !precise;
          break;
        case 1:
          byType = !byType;
          break;
        case 2:
          byMaterial = !byMaterial;
          break;
      }
    }

    public boolean matches(ItemStack target) {
      if (byType || byMaterial) {
        if (stacks.get(0).isEmpty()) {
          return false;
        }

        List<ListMatchHandler> handlers = ListRegistry.getHandlers();
        ListMatchHandler.Type type = getSortingType();
        for (ListMatchHandler h : handlers) {
          if (h.matches(type, stacks.get(0), target, precise)) {
            return true;
          }
        }
      } else {
        for (ItemStack s : stacks) {
          if (!s.isEmpty() && StackHelper.isMatchingItem(s, target, true, precise)) {
            // If precise, re-check damage is already handled by StackHelper.isMatchingItem if passed correctly
            // But BuildCraft 1.7.10 logic had an explicit damage check
            if (!precise || s.getDamageValue() == target.getDamageValue()) {
              return true;
            }
          }
        }
      }
      return false;
    }

    public ListMatchHandler.Type getSortingType() {
      return byType ? (byMaterial ? ListMatchHandler.Type.CLASS : ListMatchHandler.Type.TYPE) : ListMatchHandler.Type.MATERIAL;
    }

    public CompoundTag toNBT() {
      CompoundTag data = new CompoundTag();
      ListTag stackList = new ListTag();
      for (ItemStack stack1 : stacks) {
        CompoundTag stackNbt = new CompoundTag();
        if (!stack1.isEmpty()) {
          stack1.save(stackNbt);
        }
        stackList.add(stackNbt);
      }
      data.put("st", stackList);
      data.putBoolean("Fp", precise);
      data.putBoolean("Ft", byType);
      data.putBoolean("Fm", byMaterial);
      return data;
    }

    public void setStack(int slotIndex, ItemStack stack) {
      if (slotIndex >= 0 && slotIndex < WIDTH) {
        if (slotIndex == 0 || (!byType && !byMaterial)) {
          if (!stack.isEmpty()) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            stacks.set(slotIndex, copy);
          } else {
            stacks.set(slotIndex, ItemStack.EMPTY);
          }
        }
      }
    }

    public ItemStack getStack(int i) {
      return i >= 0 && i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
    }

    public List<ItemStack> getExamples() {
      List<ItemStack> stackList = new ArrayList<>();
      if (!stacks.get(0).isEmpty()) {
        List<ListMatchHandler> handlers = ListRegistry.getHandlers();
        List<ListMatchHandler> handlersCustom = new ArrayList<>();
        ListMatchHandler.Type type = getSortingType();
        for (ListMatchHandler h : handlers) {
          if (h.isValidSource(type, stacks.get(0))) {
            List<ItemStack> examples = h.getClientExamples(type, stacks.get(0));
            if (examples != null) {
              stackList.addAll(examples);
            } else {
              handlersCustom.add(h);
            }
          }
        }
        if (!handlersCustom.isEmpty()) {
          for (Item i : BuiltInRegistries.ITEM) {
            // Note: subItems is no longer in Item in 1.20.1.
            // Typically we'd use creative tabs or just the item itself.
            // BuildCraft 1.7.10 used getSubItems to find variations.
            // In 1.20.1 variations are often separate items or handled via tags.
            ItemStack s = new ItemStack(i);
            for (ListMatchHandler mh : handlersCustom) {
              if (mh.matches(type, stacks.get(0), s, false)) {
                stackList.add(s);
                break;
              }
            }
          }
        }

        Collections.shuffle(stackList);
      }
      return stackList;
    }
  }
}
