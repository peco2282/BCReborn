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
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.ISchematicRegistry;
import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.api.blueprints.SchematicEntity;
import com.peco2282.bcreborn.api.core.BCLog;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;

public final class SchematicRegistry implements ISchematicRegistry {

  public static final SchematicRegistry INSTANCE = new SchematicRegistry();

  public final HashMap<BlockState, SchematicFactory<? extends SchematicBlock>> schematicBlocks = new HashMap<>();
  public final HashMap<EntityType<? extends Entity>, SchematicFactory<? extends SchematicEntity>> schematicEntities = new HashMap<>();

  private final HashSet<String> modsForbidden = new HashSet<>();
  private final HashSet<String> blocksForbidden = new HashSet<>();

  private SchematicRegistry() {
  }

  @Override
  public void registerSchematicBlock(Block block, BlockState state, SchematicFactory<? extends SchematicBlock> factory) {
    if (block == null) {
      BCLog.logger.warn("Builder: Mod tried to register a null block schematic! Ignoring.");
      return;
    }
    if (schematicBlocks.containsKey(state)) {
      BCLog.logger.warn("Builder: BlockState " + state + " is already associated with a schematic. Skipping.");
      return;
    }
    try {
      schematicBlocks.put(state, factory);
    } catch (IllegalArgumentException e) {
      BCLog.logger.warn("Builder: Could not register schematic for blockstate " + state + ": " + e.getMessage());
    }
  }

  @Override
  public void registerSchematicEntity(
    EntityType<? extends Entity> type,
    SchematicFactory<? extends SchematicEntity> factory) {
    if (schematicEntities.containsKey(type)) {
      BCLog.logger.warn("Builder: Entity " + EntityType.getKey(type) + " is already associated with a schematic. Skipping.");
      return;
    }
    try {
      schematicEntities.put(type, factory);
    } catch (IllegalArgumentException e) {
      BCLog.logger.warn("Builder: Could not register schematic for entity " + EntityType.getKey(type) + ": " + e.getMessage());
    }
  }
  public SchematicBlock createSchematicBlock(Block block) {
    return createSchematicBlock(block, block.defaultBlockState());
  }

  public SchematicBlock createSchematicBlock(BlockState state) {
    return createSchematicBlock(state.getBlock(), state);
  }

  public SchematicBlock createSchematicBlock(Block block, BlockState state) {
    if (block == null) return null;

    SchematicFactory<? extends SchematicBlock> factory = schematicBlocks.get(state);
    if (factory == null) {
      // Fall back to meta 0
      factory = schematicBlocks.get(state);
    }
    if (factory == null) return null;

    SchematicBlock s = factory.create();
    s.block = block;
    return s;
  }

  public SchematicEntity createSchematicEntity(EntityType<? extends Entity> type) {
    if (!schematicEntities.containsKey(type)) return null;

    SchematicFactory<? extends SchematicEntity> c = schematicEntities.get(type);
    SchematicEntity s = c.create();
    s.entity = type;
    return s;
  }

  public boolean isAllowedForBuilding(Block block, BlockState state) {
    ResourceLocation name = BuiltInRegistries.BLOCK.getKey(block);
    String nameStr = name.toString();
    String modId = name.getNamespace();
    return isSupported(block, state)
      && !blocksForbidden.contains(nameStr)
      && !modsForbidden.contains(modId);
  }

  @Override
  public boolean isSupported(Block block, BlockState state) {
    return schematicBlocks.containsKey(state);
  }
}
