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
package com.peco2282.bcreborn.api.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface ICropHandler {
    boolean isSeed(ItemStack stack);

    boolean canSustainPlant(Level world, ItemStack seed, BlockPos pos);

    boolean plantCrop(Level world, Player player, ItemStack seed, BlockPos pos);

    boolean isMature(BlockGetter blockAccess, BlockState state, BlockPos pos);

    boolean harvestCrop(Level world, BlockPos pos, List<ItemStack> drops);
}
