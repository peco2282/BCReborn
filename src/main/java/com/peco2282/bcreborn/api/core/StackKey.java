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
package com.peco2282.bcreborn.api.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A record used as a key for storing both {@link ItemStack} and {@link FluidStack} objects in maps and collections.
 * <p>
 * This class provides a reliable hash code and equality implementation that considers item/fluid types,
 * NBT tags, and amounts. It can represent either an item stack, a fluid stack, or both.
 * </p>
 * <p>
 * Factory methods are provided for convenient construction from various types.
 * </p>
 *
 * @param stack      The ItemStack component of this key, may be null.
 * @param fluidStack The FluidStack component of this key, may be null.
 */
public record StackKey(ItemStack stack, FluidStack fluidStack) {
  public static final StackKey EMPTY = new StackKey(ItemStack.EMPTY, FluidStack.EMPTY);
  private static <T> T getOr(@Nullable T nullable, T def) {
    return nullable == null ? def : nullable;
  }

  public StackKey {
    if (stack.isEmpty() && fluidStack.isEmpty()) {
      throw new IllegalArgumentException("StackKey cannot be created with both components null");
    }
    if (!stack.isEmpty() && !fluidStack.isEmpty()) {
      throw new IllegalArgumentException("StackKey cannot be created with both components non-null");
    }
  }

  /**
   * Creates a StackKey containing only a FluidStack.
   *
   * @param fluidStack The fluid stack to store.
   */
  public StackKey(FluidStack fluidStack) {
    this(ItemStack.EMPTY, getOr(fluidStack, FluidStack.EMPTY));
  }

  /**
   * Creates a StackKey containing only an ItemStack.
   *
   * @param stack The item stack to store.
   */
  public StackKey(ItemStack stack) {
    this(getOr(stack, ItemStack.EMPTY), FluidStack.EMPTY);
  }

  /**
   * Creates a StackKey from an item and amount.
   *
   * @param item   The item type.
   * @param amount The stack size.
   * @return A new StackKey containing the specified item stack.
   */
  public static StackKey stack(Item item, int amount) {
    return new StackKey(new ItemStack(item, amount));
  }

  /**
   * Creates a StackKey from a block and amount.
   *
   * @param block  The block type.
   * @param amount The stack size.
   * @return A new StackKey containing the specified item stack.
   */
  public static StackKey stack(Block block, int amount) {
    return new StackKey(new ItemStack(block, amount));
  }

  /**
   * Creates a StackKey from an item with a stack size of 1.
   *
   * @param item The item type.
   * @return A new StackKey containing the specified item stack.
   */
  public static StackKey stack(Item item) {
    return new StackKey(new ItemStack(item, 1));
  }

  /**
   * Creates a StackKey from a block with a stack size of 1.
   *
   * @param block The block type.
   * @return A new StackKey containing the specified item stack.
   */
  public static StackKey stack(Block block) {
    return new StackKey(new ItemStack(block, 1));
  }

  /**
   * Creates a StackKey from an existing ItemStack.
   *
   * @param itemStack The item stack to store.
   * @return A new StackKey containing the specified item stack.
   */
  public static StackKey stack(ItemStack itemStack) {
    return new StackKey(itemStack);
  }

  /**
   * Creates a StackKey from a fluid and amount.
   *
   * @param fluid  The fluid type.
   * @param amount The fluid amount.
   * @return A new StackKey containing the specified fluid stack.
   */
  public static StackKey fluid(Fluid fluid, int amount) {
    return new StackKey(new FluidStack(fluid, amount));
  }

  /**
   * Creates a StackKey from a fluid with an amount of 1.
   *
   * @param fluid The fluid type.
   * @return A new StackKey containing the specified fluid stack.
   */
  public static StackKey fluid(Fluid fluid) {
    return new StackKey(new FluidStack(fluid, 1));
  }

  /**
   * Creates a StackKey from an existing FluidStack.
   *
   * @param fluidStack The fluid stack to store.
   * @return A new StackKey containing the specified fluid stack.
   */
  public static StackKey fluid(FluidStack fluidStack) {
    return new StackKey(fluidStack);
  }

  /**
   * Compares this StackKey to another object for equality.
   * <p>
   * Two StackKeys are equal if:
   * <ul>
   * <li>Both have the same item type and NBT tags (if item stacks are present)</li>
   * <li>Both have the same fluid type, amount, and NBT tags (if fluid stacks are present)</li>
   * <li>Both have null for the same stack types</li>
   * </ul>
   * </p>
   *
   * @param o The object to compare to.
   * @return True if the objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || o.getClass() != StackKey.class) {
      return false;
    }
    StackKey k = (StackKey) o;
    return ItemStack.isSameItemSameTags(stack, k.stack) && FluidStack.areFluidStackTagsEqual(fluidStack, k.fluidStack);
  }

  /**
   * Computes a hash code for this StackKey based on its item and fluid components.
   * <p>
   * The hash code considers item types, fluid types, amounts, and NBT tags.
   * </p>
   *
   * @return The computed hash code.
   */
  @Override
  public int hashCode() {
    int result = 7;
    if (stack != null) {
      result = 31 * result + stack.getItem().hashCode();
      result = 31 * result + stack.getCount();
      result = 31 * result + Objects.hashCode(stack.getTag());
    }
    result = 31 * result + 7;
    if (fluidStack != null) {
      result = 31 * result + fluidStack.getFluid().hashCode();
      result = 31 * result + fluidStack.getAmount();
      result = 31 * result + Objects.hashCode(fluidStack.getTag());
    }
    return result;
  }

  /**
   * Creates a deep copy of this StackKey.
   * <p>
   * Both the ItemStack and FluidStack (if present) are copied.
   * </p>
   *
   * @return A new StackKey with copied stacks.
   */
  public StackKey copy() {
    return new StackKey(stack.copy(), fluidStack.copy());
  }
}
