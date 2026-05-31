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
package com.peco2282.bcreborn.api.transport;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public enum PipeWire implements StringRepresentable {
  RED, BLUE, GREEN, YELLOW;

  public static final PipeWire[] VALUES = values();
  public static Item item;

  public static PipeWire fromOrdinal(int ordinal) {
    if (ordinal < 0 || ordinal >= VALUES.length) {
      return RED;
    } else {
      return VALUES[ordinal];
    }
  }

  public PipeWire reverse() {
    return switch (this) {
      case RED -> YELLOW;
      case BLUE -> GREEN;
      case GREEN -> BLUE;
      default -> RED;
    };
  }

  public String getTag() {
    return name().toLowerCase(Locale.ENGLISH) + "PipeWire";
  }

  public String getColor() {
    String name = this.toString().toLowerCase(Locale.ENGLISH);
    char first = Character.toUpperCase(name.charAt(0));
    return first + name.substring(1);
  }

  public ItemStack getStack() {
    return getStack(1);
  }

  public ItemStack getStack(int qty) {
    if (item == null) {
      return ItemStack.EMPTY;
    } else {
      return new ItemStack(item, qty);
    }
  }

  public boolean isPipeWire(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    } else {
      return stack.getItem() == item;
    }
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
