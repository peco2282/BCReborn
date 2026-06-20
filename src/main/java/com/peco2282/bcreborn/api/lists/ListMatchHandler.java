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
package com.peco2282.bcreborn.api.lists;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ListMatchHandler {
  public abstract boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise);

  public abstract boolean isValidSource(Type type, ItemStack stack);

  @Nullable
  public List<ItemStack> getClientExamples(Type type, ItemStack stack) {
    return null;
  }

  public enum Type {
    TYPE, MATERIAL, CLASS
  }
}
