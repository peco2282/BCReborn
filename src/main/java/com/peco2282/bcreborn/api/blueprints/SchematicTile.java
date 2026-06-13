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
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;

/**
 * Schematic implementation for blocks with block entities.
 */
public class SchematicTile extends SchematicBlock {

  /**
   * The NBT data of the block entity.
   */
  public CompoundTag tileNBT = new CompoundTag();

  @Override
  public void idsToBlueprint(MappingRegistry registry) {
    registry.scanAndTranslateStacksToRegistry(tileNBT);
  }

  @Override
  public void idsToWorld(MappingRegistry registry) {
    try {
      registry.scanAndTranslateStacksToWorld(tileNBT);
    } catch (MappingNotFoundException e) {
      tileNBT = new CompoundTag();
    }
  }

  /**
   * Called after the NBT data has been loaded.
   */
  public void onNBTLoaded() {

  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    super.placeInWorld(context, x, y, z, stacks);

    BlockPos pos = new BlockPos(x, y, z);
    BlockEntity be = context.world().getBlockEntity(pos);
    if (be != null) {
      be.load(tileNBT);
    }
  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    super.initializeFromObjectAt(context, x, y, z);

    BlockEntity tile = context.world().getBlockEntity(new BlockPos(x, y, z));
    if (tile != null) {
      tileNBT = tile.saveWithFullMetadata();
      onNBTLoaded();
    }
  }

  @Override
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
    super.writeSchematicToNBT(nbt, registry);

    nbt.put("blockCpt", tileNBT);
  }

  @Override
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
    super.readSchematicFromNBT(nbt, registry);

    tileNBT = nbt.getCompound("blockCpt");
    onNBTLoaded();
  }

  @Override
  public int buildTime() {
    return 5;
  }
}
