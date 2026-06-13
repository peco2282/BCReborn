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
package com.peco2282.bcreborn.api.recipes;

import org.jetbrains.annotations.ApiStatus;

/**
 * Central registry for Buildcraft recipe managers.
 * <p>
 * Provides access to recipe managers for assembly tables, integration tables,
 * refineries, and programming tables.
 */
public final class BuildcraftRecipeRegistry {

  /**
   * The assembly table recipe manager.
   */
  public static IAssemblyRecipeManager assemblyTable;

  /**
   * The integration table recipe manager.
   */
  public static IIntegrationRecipeManager integrationTable;

  /**
   * The refinery recipe manager.
   */
  public static IRefineryRecipeManager refinery;

  /**
   * The programming table recipe manager.
   */
  public static IProgrammingRecipeManager programmingTable;

  private BuildcraftRecipeRegistry() {
  }

  /**
   * Gets the assembly table recipe manager.
   *
   * @return The assembly table recipe manager.
   */
  public static IAssemblyRecipeManager assembly() {
    return assemblyTable;
  }

  /**
   * Gets the integration table recipe manager.
   *
   * @return The integration table recipe manager.
   */
  public static IIntegrationRecipeManager integration() {
    return integrationTable;
  }

  /**
   * Gets the refinery recipe manager.
   *
   * @return The refinery recipe manager.
   */
  public static IRefineryRecipeManager refinery() {
    return refinery;
  }

  /**
   * Gets the programming table recipe manager.
   *
   * @return The programming table recipe manager.
   */
  public static IProgrammingRecipeManager programming() {
    return programmingTable;
  }

  @ApiStatus.Internal
  public static void assembly(IAssemblyRecipeManager manager) {
    assemblyTable = manager;
  }

  @ApiStatus.Internal
  public static void integration(IIntegrationRecipeManager manager) {
    integrationTable = manager;
  }

  @ApiStatus.Internal
  public static void refinery(IRefineryRecipeManager manager) {
    refinery = manager;
  }

  @ApiStatus.Internal
  public static void programming(IProgrammingRecipeManager manager) {
    programmingTable = manager;
  }
}
