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
import org.jetbrains.annotations.Nullable;


/**
 * Registry for associating blocks and entities with their corresponding schematic factories.
 */
public interface ISchematicRegistry {
  /**
   * Registers a schematic factory for a specific block.
   *
   * @param block   The block.
   * @param factory The schematic factory.
   */
  default void registerSchematicBlock(Block block, SchematicFactory<? extends SchematicBlock> factory) {
    registerSchematicBlock(block, block.defaultBlockState(), factory);
  }

  /**
   * Registers a schematic factory for a specific block state or block.
   *
   * @param block   The block.
   * @param state   The block state.
   * @param factory The schematic factory.
   */
  void registerSchematicBlock(@Nullable Block block, @Nullable BlockState state, SchematicFactory<? extends SchematicBlock> factory);

  /**
   * Registers a schematic factory for a specific entity type.
   *
   * @param entityClass The entity type.
   * @param factory     The schematic factory.
   */
  void registerSchematicEntity(
    EntityType<? extends Entity> entityClass,
    SchematicFactory<? extends SchematicEntity> factory);

  /**
   * Checks if a schematic is registered for the given block state.
   *
   * @param state The block state.
   * @return True if supported.
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean isSupported(BlockState state);

  /**
   * Factory interface for creating schematic instances.
   *
   * @param <T> The type of schematic.
   */
  @FunctionalInterface
  interface SchematicFactory<T extends Schematic> {
    /**
     * Creates a new schematic instance.
     *
     * @return The schematic.
     */
    T create();
  }
}
