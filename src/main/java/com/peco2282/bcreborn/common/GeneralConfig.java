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

import com.peco2282.bcreborn.common.config.ConfigEntry;
import com.peco2282.bcreborn.common.config.ConfigSection;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class GeneralConfig {
  // display
  private static ForgeConfigSpec.BooleanValue colorBlindMode;
  private static ForgeConfigSpec.BooleanValue hideFluidValues;
  private static ForgeConfigSpec.BooleanValue hidePowerValues;

  // debug
  private static ForgeConfigSpec.BooleanValue printFacadeList;
  private static ForgeConfigSpec.BooleanValue printBlueprintSchematicList;

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
    printBlueprintSchematicList = builder.comment("Print a list of all registered blueprint schematics.")
      .define("printBlueprintSchematicList", false);
    builder.pop();

    builder.pop();
    return builder;
  }

  public static ConfigSection[] entries() {
    var display = ConfigSection.builder(
      Component.translatable("screen.config.section.general.display.title")
    ).addEntries(
      ConfigEntry.booleanOf(
      "colorBlindMode",
      Component.translatable("screen.config.entry.general.colorBlindMode.title"),
      Component.translatable("screen.config.entry.general.colorBlindMode.tooltip"),
      colorBlindMode
    ),
      ConfigEntry.booleanOf(
        "hideFluidValues",
        Component.translatable("screen.config.entry.general.hideFluidValues.title"),
        Component.translatable("screen.config.entry.general.hideFluidValues.tooltip"),
        hideFluidValues
      ),
      ConfigEntry.booleanOf(
        "hidePowerValues",
        Component.translatable("screen.config.entry.general.hidePowerValues.title"),
        Component.translatable("screen.config.entry.general.hidePowerValues.tooltip"),
        hidePowerValues
      )).build();

    var debug = ConfigSection.builder(
      Component.translatable("screen.config.section.general.debug.title")
    ).addEntries(
      ConfigEntry.booleanOf(
      "printFacadeList",
      Component.translatable("screen.config.entry.general.printFacadeList.title"),
      Component.translatable("screen.config.entry.general.printFacadeList.tooltip"),
      printFacadeList
    ),
      ConfigEntry.booleanOf(
        "printBlueprintSchematicList",
        Component.translatable("screen.config.entry.general.printBlueprintSchematicList.title"),
        Component.translatable("screen.config.entry.general.printBlueprintSchematicList.tooltip"),
        printBlueprintSchematicList
      )
    ).build();

    return new ConfigSection[] { display, debug };
  }
}
