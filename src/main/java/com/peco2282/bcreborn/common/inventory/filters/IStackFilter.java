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
package com.peco2282.bcreborn.common.inventory.filters;


import net.minecraft.world.item.ItemStack;

/**
 * This interface provides a convenient means of dealing with entire classes of
 * items without having to specify each item individually.
 */
public interface IStackFilter {

  boolean matches(ItemStack stack);
}
