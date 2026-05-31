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
package com.peco2282.bcreborn.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigGeneral {
  // display
  private static ForgeConfigSpec.BooleanValue colorBlindMode;
  private static ForgeConfigSpec.BooleanValue hideFluidValues;
  private static ForgeConfigSpec.BooleanValue hidePowerValues;

  // debug
  private static ForgeConfigSpec.BooleanValue printFacadeList;
  private static ForgeConfigSpec.BooleanValue printBlueprintSchematicList;

  // experimental
  private static ForgeConfigSpec.BooleanValue kinesisPowerLossOnTravel;

  public static boolean isColorBlindMode() {
    return colorBlindMode.get();
  }

  public static boolean isHideFluidValues() {
    return hideFluidValues.get();
  }

  public static boolean isHidePowerValues() {
    return hidePowerValues.get();
  }

  public static boolean isPrintFacadeList() {
    return printFacadeList.get();
  }

  public static boolean isPrintBlueprintSchematicList() {
    return printBlueprintSchematicList.get();
  }

  public static boolean isKinesisPowerLossOnTravel() {
    return kinesisPowerLossOnTravel.get();
  }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("General settings").push("general");

    builder.comment("Display settings").push("display");
    colorBlindMode = builder.comment("Should I enable colorblind mode?")
      .define("colorBlindMode", false);
    hideFluidValues = builder.comment("Should all fluid values (mB, mB/t) be hidden?")
      .define("hideFluidValues", false);
    hidePowerValues = builder.comment("Should all power values (RF, RF/t) be hidden?")
      .define("hidePowerValues", false);
    builder.pop();

    builder.comment("Debug settings").push("debug");
    printFacadeList = builder.comment("Print a list of all registered facades.")
      .define("printFacadeList", false);
    printBlueprintSchematicList = builder
      .define("printBlueprintSchematicList", false);
    builder.pop();

    builder.comment("Experimental settings").push("experimental");
    kinesisPowerLossOnTravel = builder.comment("Should kinesis pipes lose power over distance (think IC2 or BC pre-3.7)?")
      .define("kinesisPowerLossOnTravel", false);
    builder.pop();

    builder.pop();
    return builder;
  }
}
