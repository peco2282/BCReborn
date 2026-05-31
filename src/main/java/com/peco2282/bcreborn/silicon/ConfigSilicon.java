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
package com.peco2282.bcreborn.silicon;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ConfigSilicon {
  // items
  private static ForgeConfigSpec.BooleanValue redstoneChipset;
  private static ForgeConfigSpec.BooleanValue redstoneCrystal;
  private static ForgeConfigSpec.BooleanValue redstoneBoard;
  private static ForgeConfigSpec.BooleanValue gateCopier;
  private static ForgeConfigSpec.BooleanValue pipeGate;
  private static ForgeConfigSpec.BooleanValue pipeLens;
  private static ForgeConfigSpec.BooleanValue pipeWire;

  // blocks
  private static ForgeConfigSpec.BooleanValue laserBlock;
  private static ForgeConfigSpec.BooleanValue laserTableBlock;
  private static ForgeConfigSpec.BooleanValue libraryBlock;
  private static ForgeConfigSpec.BooleanValue zonePlan;

  public static boolean isRedstoneChipset() {
    return redstoneChipset.get();
  }

  public static boolean isRedstoneCrystal() {
    return redstoneCrystal.get();
  }

  public static boolean isRedstoneBoard() {
    return redstoneBoard.get();
  }

  public static boolean isGateCopier() {
    return gateCopier.get();
  }

  public static boolean isPipeGate() {
    return pipeGate.get();
  }

  public static boolean isPipeLens() {
    return pipeLens.get();
  }

  public static boolean isPipeWire() {
    return pipeWire.get();
  }

  public static boolean isLaserBlock() {
    return laserBlock.get();
  }

  public static boolean isLaserTableBlock() {
    return laserTableBlock.get();
  }

  public static boolean isLibraryBlock() {
    return libraryBlock.get();
  }

  public static boolean isZonePlan() {
    return zonePlan.get();
  }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Silicon settings").push("silicon");

    builder.comment("Silicon item enable/disable").push("items");
    redstoneChipset = builder.define("redstoneChipset", true);
    redstoneCrystal = builder.define("redstoneCrystal", true);
    redstoneBoard = builder.define("redstone_board", true);
    gateCopier = builder.define("gateCopier", true);
    pipeGate = builder.define("pipeGate", true);
    pipeLens = builder.define("pipeLens", true);
    pipeWire = builder.define("pipeWire", true);
    builder.pop();

    builder.comment("Silicon block enable/disable").push("blocks");
    laserBlock = builder.define("laserBlock", true);
    laserTableBlock = builder.define("laserTableBlock", true);
    libraryBlock = builder.define("libraryBlock", true);
    zonePlan = builder.define("zonePlan", true);
    builder.pop();

    builder.pop();
    return builder;
  }
}
