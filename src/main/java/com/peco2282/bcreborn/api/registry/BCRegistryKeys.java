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
package com.peco2282.bcreborn.api.registry;

import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.core.IWorldProperty;
import com.peco2282.bcreborn.api.crops.ICropHandler;
import com.peco2282.bcreborn.api.facades.IFacadeItem;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Registry keys for BuildCraft Reborn custom registries.
 * <p>
 * This class defines {@link ResourceKey} instances for all custom registries
 * used throughout the BC Reborn API. Each registry stores specific types of
 * game objects such as schematics, statements, and crop handlers.
 * </p>
 */
public class BCRegistryKeys {
  /**
   * Registry key for {@link Schematic} objects.
   * <p>
   * Schematics define blueprint patterns used for construction and building.
   * </p>
   */
  public static final ResourceKey<Registry<Schematic>> SCHEMATIC = key("schematic");
  /**
   * Registry key for {@link IWorldProperty} objects.
   * <p>
   * World properties define custom data associated with world state.
   * </p>
   */
  public static final ResourceKey<Registry<IWorldProperty>> WORLD_PROPERTY = key("world_property");
  /**
   * Registry key for {@link ICropHandler} objects.
   * <p>
   * Crop handlers manage interactions with various crop types in the game.
   * </p>
   */
  public static final ResourceKey<Registry<ICropHandler>> CROP_HANDLER = key("crop_handler");
  /**
   * Registry key for {@link IFacadeItem} objects.
   * <p>
   * Facade items are used to decorate pipes and other BuildCraft structures.
   * </p>
   */
  public static final ResourceKey<Registry<IFacadeItem>> FACADE_ITEM = key("facade_item");
  /**
   * Registry key for {@link IStatement} objects.
   * <p>
   * Statements define conditions and actions for programmable gates and robots.
   * </p>
   */
  public static final ResourceKey<Registry<IStatement>> STATEMENT = key("statement");
  /**
   * Registry key for {@link IStatementParameter} objects.
   * <p>
   * Statement parameters provide configurable inputs for statements.
   * </p>
   */
  public static final ResourceKey<Registry<IStatementParameter>> STATEMENT_PARAMETER = key("statement_parameter");
  /**
   * Registry key for {@link RedstoneBoardNBT} objects.
   * <p>
   * Redstone boards define programmable behavior for robot AI.
   * </p>
   */
  public static final ResourceKey<Registry<RedstoneBoardNBT<?>>> REDSTONE_BOARD = key("redstone_board");


  /**
   * Creates a registry key for the specified registry name.
   * <p>
   * All registry keys are created under the "bcreborn" namespace.
   * </p>
   *
   * @param name The name of the registry.
   * @param <T>  The type of objects stored in the registry.
   * @return A {@link ResourceKey} for the registry.
   */
  private static <T> ResourceKey<Registry<T>> key(String name) {
    return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("bcreborn", name));
  }
}
