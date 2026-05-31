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
package com.peco2282.bcreborn.api.fuels;


public final class BuildcraftFuelRegistry {
  private static IFuelManager fuel;

  private static ICoolantManager coolant;

  private BuildcraftFuelRegistry() {
  }

  public static IFuelManager getFuelManager() {
    return fuel;
  }

  public static void setFuelManager(IFuelManager manager) {
    if (fuel != null) {
      throw new IllegalStateException("Fuel manager already set");
    }
    fuel = manager;
  }

  public static ICoolantManager getCoolantManager() {
    return coolant;
  }

  public static void setCoolantManager(ICoolantManager manager) {
    if (coolant != null) {
      throw new IllegalStateException("Coolant manager already set");
    }
    coolant = manager;
  }
}
