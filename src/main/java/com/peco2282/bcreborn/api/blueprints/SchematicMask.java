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
package com.peco2282.bcreborn.api.blueprints;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;

public class SchematicMask extends SchematicBlockBase {

  public boolean isConcrete = true;

  public SchematicMask() {

  }

  public SchematicMask(boolean isConcrete) {
    this.isConcrete = isConcrete;
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    BlockPos pos = new BlockPos(x, y, z);
    if (isConcrete) {
      if (stacks.isEmpty() || !isReplaceable(context, pos)) {
        return;
      }
      context.world().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    } else {
      context.world().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }
  }

  @Override
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    if (isConcrete) {
      return !isReplaceable(context, pos);
    } else {
      return isReplaceable(context, pos);
    }
  }

  private boolean isReplaceable(IBuilderContext context, BlockPos pos) {
    BlockState state = context.world().getBlockState(pos);
    return state.isAir() || state.canBeReplaced();
  }

  @Override
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
    nbt.putBoolean("isConcrete", isConcrete);
  }

  @Override
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
    isConcrete = nbt.getBoolean("isConcrete");
  }
}
