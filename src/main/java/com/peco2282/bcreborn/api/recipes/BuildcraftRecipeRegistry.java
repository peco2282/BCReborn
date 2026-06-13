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

public final class BuildcraftRecipeRegistry {

  public static IAssemblyRecipeManager assemblyTable;
  public static IIntegrationRecipeManager integrationTable;
  public static IRefineryRecipeManager refinery;
  public static IProgrammingRecipeManager programmingTable;

  public static IAssemblyRecipeManager assembly() { return assemblyTable; }
  public static IIntegrationRecipeManager integration() { return integrationTable; }
  public static IRefineryRecipeManager refinery() { return refinery; }
  public static IProgrammingRecipeManager programming() { return programmingTable; }

  @ApiStatus.Internal
  public static void assembly(IAssemblyRecipeManager manager) { assemblyTable = manager; }
  @ApiStatus.Internal
  public static void integration(IIntegrationRecipeManager manager) { integrationTable = manager; }
  @ApiStatus.Internal
  public static void refinery(IRefineryRecipeManager manager) { refinery = manager; }
  @ApiStatus.Internal
  public static void programming(IProgrammingRecipeManager manager) { programmingTable = manager; }

  private BuildcraftRecipeRegistry() {
  }
}
