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


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ISchematicRegistry {
  default void registerSchematicBlock(Block block, SchematicFactory<? extends SchematicBlock> factory) {
    registerSchematicBlock(block, block.defaultBlockState(), factory);
  }

  void registerSchematicBlock(Block block, BlockState state, SchematicFactory<? extends SchematicBlock> factory);

  void registerSchematicEntity(
    EntityType<? extends Entity> entityClass,
    SchematicFactory<? extends SchematicEntity> factory);

  boolean isSupported(Block block, BlockState state);

  @FunctionalInterface
  interface SchematicFactory<T extends Schematic> {
    T create();
  }
}
