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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ISchematicRegistry {
  default void registerSchematicBlock(@NotNull Block block, @NotNull SchematicFactory<? extends SchematicBlock> factory) {
    registerSchematicBlock(block, block.defaultBlockState(), factory);
  }

  void registerSchematicBlock(@Nullable Block block, @Nullable BlockState state, @NotNull SchematicFactory<? extends SchematicBlock> factory);

  void registerSchematicEntity(
    @NotNull EntityType<? extends Entity> entityClass,
    @NotNull SchematicFactory<? extends SchematicEntity> factory);

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean isSupported(@NotNull BlockState state);

  @FunctionalInterface
  interface SchematicFactory<T extends Schematic> {
    @NotNull T create();
  }
}
