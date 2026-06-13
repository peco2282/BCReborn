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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CropManager {
  private static final List<ICropHandler> HANDLERS = new ArrayList<>();

  private CropManager() {
  }

  public static List<ICropHandler> getHandlers() {
    return Collections.unmodifiableList(HANDLERS);
  }

  public static void register(ICropHandler cropHandler) {
    HANDLERS.add(cropHandler);
  }

  public static boolean isSeed(ItemStack stack) {
    for (ICropHandler cropHandler : HANDLERS) {
      if (cropHandler.isSeed(stack)) {
        return true;
      }
    }
    return false;
  }

  public static boolean canSustainPlant(Level world, ItemStack seed, BlockPos pos) {
    for (ICropHandler cropHandler : HANDLERS) {
      if (cropHandler.isSeed(seed) && cropHandler.canSustainPlant(world, seed, pos)) {
        return true;
      }
    }
    return false;
  }

  public static boolean plantCrop(Level world, Player player, ItemStack seed, BlockPos pos) {
    for (ICropHandler cropHandler : HANDLERS) {
      if (cropHandler.isSeed(seed) && cropHandler.canSustainPlant(world, seed, pos)
        && cropHandler.plantCrop(world, player, seed, pos)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isMature(BlockGetter blockAccess, BlockState state, BlockPos pos) {
    for (ICropHandler cropHandler : HANDLERS) {
      if (cropHandler.isMature(blockAccess, state, pos)) {
        return true;
      }
    }
    return false;
  }

  public static boolean harvestCrop(Level world, BlockPos pos, List<ItemStack> drops) {
    BlockState state = world.getBlockState(pos);
    for (ICropHandler cropHandler : HANDLERS) {
      if (cropHandler.isMature(world, state, pos)) {
        return cropHandler.harvestCrop(world, pos, drops);
      }
    }
    return false;
  }
}
