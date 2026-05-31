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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;

public class ListMatchHandlerTools extends ListMatchHandler {
  @Override
  public boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise) {
    if (type == Type.TYPE) {
      if (stack.getItem() instanceof TieredItem source && target.getItem() instanceof TieredItem tieredTarget) {
        if (precise) {
          return source.getTier() == tieredTarget.getTier() && source.getClass() == tieredTarget.getClass();
        } else {
          return source.getClass() == tieredTarget.getClass();
        }
      }
    }
    return false;
  }

  @Override
  public boolean isValidSource(Type type, ItemStack stack) {
    return stack.getItem() instanceof TieredItem;
  }
}
