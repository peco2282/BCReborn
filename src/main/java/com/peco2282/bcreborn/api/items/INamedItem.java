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

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface INamedItem {
    Component getName(ItemStack stack);

    boolean setName(ItemStack stack, Component name);
}
