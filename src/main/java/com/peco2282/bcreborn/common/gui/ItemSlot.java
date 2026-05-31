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
package com.peco2282.bcreborn.common.gui;

import net.minecraft.world.item.ItemStack;

public class ItemSlot extends AdvancedSlot {
  public ItemStack stack;

  public ItemSlot(GuiAdvancedInterface<?> gui, int x, int y) {
    super(gui, x, y);
  }

  public ItemSlot(GuiAdvancedInterface<?> gui, int x, int y, ItemStack iStack) {
    super(gui, x, y);
    stack = iStack;
  }

  @Override
  public ItemStack getItemStack() {
    return stack;
  }
}
