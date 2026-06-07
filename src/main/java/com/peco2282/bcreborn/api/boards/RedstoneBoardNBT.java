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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class RedstoneBoardNBT<T> {
  public static final int COST_ZERO = 0;
  public static final int COST_LOW = 8_000;
  public static final int COST_MEDIUM = 32_000;
  public static final int COST_HIGH = 128_000;
  public static final int COST_VERY_HIGH = 512_000;
  private static final Random rand = new Random();
  private static Supplier<? extends RedstoneBoardNBT<?>> emptyObject;

  public static Supplier<? extends RedstoneBoardNBT<?>> getEmpty() {
    return emptyObject != null ? emptyObject : () -> null;
  }

  public static void setEmpty(Supplier<? extends RedstoneBoardNBT<?>> empty) {
    if (emptyObject == null) {
      emptyObject = empty;
    } else {
      throw new IllegalStateException("RedstoneBoardNBT empty supplier is already set");
    }
  }

  public abstract int getEnergyCost();

  public abstract ResourceLocation getID();

  public abstract void addInformation(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag);

  public abstract IRedstoneBoard<T> create(CompoundTag nbt, T object);

  public void createBoard(CompoundTag nbt) {
    nbt.putString("id", getID().toString());
  }

  public int getParameterNumber(CompoundTag nbt) {
    if (!nbt.contains("parameters")) {
      return 0;
    } else {
      return nbt.getList("parameters", 10).size();
    }
  }

  public float nextFloat(int difficulty) {
    return 1F - (float) Math.pow(rand.nextFloat(), 1F / difficulty);
  }
}
