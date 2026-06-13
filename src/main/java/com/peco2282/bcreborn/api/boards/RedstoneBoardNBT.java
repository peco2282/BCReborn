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
package com.peco2282.bcreborn.api.boards;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Abstract base class for redstone board implementations that use NBT data storage.
 * <p>
 * This class provides the framework for creating custom redstone boards with configurable
 * energy costs, persistent data storage, and tooltip information. Subclasses define specific
 * board behaviors and properties.
 * </p>
 *
 * @param <T> The type of object this board operates on.
 */
public abstract class RedstoneBoardNBT<T> {
  /**
   * Energy cost constant representing zero energy consumption.
   */
  public static final int COST_ZERO = 0;

  /**
   * Energy cost constant representing low energy consumption (8,000 units).
   */
  public static final int COST_LOW = 8_000;

  /**
   * Energy cost constant representing medium energy consumption (32,000 units).
   */
  public static final int COST_MEDIUM = 32_000;

  /**
   * Energy cost constant representing high energy consumption (128,000 units).
   */
  public static final int COST_HIGH = 128_000;

  /**
   * Energy cost constant representing very high energy consumption (512,000 units).
   */
  public static final int COST_VERY_HIGH = 512_000;
  private static final Random rand = new Random();
  private static Supplier<? extends RedstoneBoardNBT<?>> emptyObject;

  /**
   * Gets the supplier for the empty board instance.
   *
   * @return A supplier that provides an empty RedstoneBoardNBT instance, or a null-returning supplier if not set.
   */
  public static Supplier<? extends RedstoneBoardNBT<?>> getEmpty() {
    return emptyObject != null ? emptyObject : () -> null;
  }

  /**
   * Sets the supplier for the empty board instance.
   * <p>
   * This method can only be called once. Subsequent calls will throw an exception.
   * </p>
   *
   * @param empty The supplier for the empty board instance.
   * @throws IllegalStateException if the empty supplier has already been set.
   */
  @ApiStatus.Internal
  public static void setEmpty(Supplier<? extends RedstoneBoardNBT<?>> empty) {
    if (emptyObject == null) {
      emptyObject = empty;
    } else {
      throw new IllegalStateException("RedstoneBoardNBT emptySupplier supplier is already set");
    }
  }

  /**
   * Gets the energy cost required to use this board.
   *
   * @return The energy cost in units. Consider using the predefined COST_* constants.
   */
  public abstract int getEnergyCost();

  /**
   * Gets the unique identifier for this board type.
   *
   * @return The ResourceLocation representing this board's ID.
   */
  public abstract ResourceLocation getID();

  /**
   * Adds tooltip information to the board item.
   * <p>
   * This method is called when the player hovers over the board item in their inventory.
   * </p>
   *
   * @param stack   The ItemStack representing the board.
   * @param level   The current level/world, may be null.
   * @param tooltip The list of tooltip components to add information to.
   * @param flag    The tooltip flag indicating advanced tooltip mode.
   */
  public abstract void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag);

  /**
   * Creates an instance of the board with the specified NBT data and object.
   *
   * @param nbt    The NBT data containing board configuration.
   * @param object The object this board will operate on.
   * @return A new IRedstoneBoard instance configured with the provided data.
   */
  public abstract IRedstoneBoard<T> create(CompoundTag nbt, T object);

  /**
   * Initializes the board's NBT data with its identifier.
   * <p>
   * This method writes the board's ID to the provided NBT tag. Override this method
   * to add additional initialization data.
   * </p>
   *
   * @param nbt The NBT tag to write the board ID to.
   */
  public void createBoard(CompoundTag nbt) {
    nbt.putString("id", getID().toString());
  }

  /**
   * Gets the number of parameters stored in the NBT data.
   *
   * @param nbt The NBT tag to read parameters from.
   * @return The number of parameters, or 0 if no parameters are present.
   */
  public int getParameterNumber(CompoundTag nbt) {
    if (!nbt.contains("parameters")) {
      return 0;
    } else {
      return nbt.getList("parameters", 10).size();
    }
  }

  /**
   * Generates a random float value weighted by difficulty.
   * <p>
   * Higher difficulty values produce values closer to 1.0. This can be used for
   * randomized board behaviors that scale with difficulty.
   * </p>
   *
   * @param difficulty The difficulty level, must be greater than 0.
   * @return A random float value between 0.0 and 1.0, weighted toward 1.0 as difficulty increases.
   */
  public float nextFloat(int difficulty) {
    return 1F - (float) Math.pow(rand.nextFloat(), 1F / difficulty);
  }
}
