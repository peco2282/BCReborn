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
package com.peco2282.bcreborn.api.items;

import net.minecraft.world.item.ItemStack;

/**
 * Interface for items that represent blueprints or templates.
 * Extends {@link INamedItem}.
 */
public interface IBlueprintItem extends INamedItem {

  /**
   * Gets the type of the blueprint item.
   *
   * @param stack The item stack.
   * @return The {@link Type}.
   */
  Type getType(ItemStack stack);

  /**
   * Represents the type of blueprint item.
   */
  enum Type {
    TEMPLATE, BLUEPRINT
  }
}
