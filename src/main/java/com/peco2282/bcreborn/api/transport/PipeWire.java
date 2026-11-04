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
import org.jetbrains.annotations.Nullable;

/**
 * Represents different colors of pipe wires.
 */
public enum PipeWire implements StringRepresentable {
  RED, BLUE, GREEN, YELLOW;

  /**
   * Cache of all possible pipe wire values.
   */
  public static final PipeWire[] VALUES = values();

  /**
   * The original used one item with a damage value per colour. 1.20 has no damage subtypes, so each colour has its
   * own item ({@link #setItem}); this field is kept for compatibility and is the red wire item.
   */
  @Deprecated
  public static Item item;

  private Item wireItem;

  /** Registers the item of this wire colour. */
  public void setItem(Item wireItem) {
    this.wireItem = wireItem;
    if (this == RED) {
      item = wireItem;
    }
  }

  @Nullable
  public Item getItem() {
    return wireItem;
  }

  /** @return the wire colour of {@code stack}, or null if it is not a pipe wire */
  @Nullable
  public static PipeWire fromStack(ItemStack stack) {
    if (stack.isEmpty()) return null;
    for (PipeWire wire : VALUES) {
      if (wire.wireItem != null && stack.getItem() == wire.wireItem) return wire;
    }
    return null;
  }

  /**
   * Gets a pipe wire from its ordinal ID.
   *
   * @param ordinal The ordinal ID.
   * @return The pipe wire, or {@link #RED} if out of bounds.
   */
  public static PipeWire fromOrdinal(int ordinal) {
    if (ordinal < 0 || ordinal >= VALUES.length) {
      return RED;
    } else {
      return VALUES[ordinal];
    }
  }

  /**
   * @return The "reverse" color of the wire.
   */
  public PipeWire reverse() {
    return switch (this) {
      case RED -> YELLOW;
      case BLUE -> GREEN;
      case GREEN -> BLUE;
      default -> RED;
    };
  }

  /**
   * @return The lowercase name of the wire with "PipeWire" suffix, suitable for tags.
   */
  public String getTag() {
    return name().toLowerCase(Locale.ENGLISH) + "PipeWire";
  }

  /**
   * @return The capitalized name of the color (e.g., "Red").
   */
  public String getColor() {
    String name = this.toString().toLowerCase(Locale.ENGLISH);
    char first = Character.toUpperCase(name.charAt(0));
    return first + name.substring(1);
  }

  /**
   * @return An {@link ItemStack} of this pipe wire with a count of 1.
   */
  public ItemStack getStack() {
    return getStack(1);
  }

  /**
   * Gets an {@link ItemStack} of this pipe wire with the specified quantity.
   *
   * @param qty The quantity.
   * @return The stack, or {@link ItemStack#EMPTY} if the item is not registered.
   */
  public ItemStack getStack(int qty) {
    if (wireItem == null) {
      return ItemStack.EMPTY;
    } else {
      return new ItemStack(wireItem, qty);
    }
  }

  /**
   * Checks if the given stack is a pipe wire item.
   *
   * @param stack The stack to check.
   * @return True if it is a pipe wire.
   */
  public boolean isPipeWire(ItemStack stack) {
    // Original also compared the damage value (colour); each colour now has its own item.
    return !stack.isEmpty() && wireItem != null && stack.getItem() == wireItem;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
