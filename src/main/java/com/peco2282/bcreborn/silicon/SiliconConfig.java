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

import com.peco2282.bcreborn.common.config.ConfigEntry;
import com.peco2282.bcreborn.common.config.ConfigSection;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class SiliconConfig {
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

  // power
  private static ForgeConfigSpec.DoubleValue chipsetCostMultiplier;

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

  public static double chipsetCostMultiplier() {
    return chipsetCostMultiplier.get();
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

    builder.comment("Power settings").push("power");
    chipsetCostMultiplier = builder.comment("The cost multiplier for Chipsets")
      .defineInRange("chipsetCostMultiplier", 1.0, 0.0, Double.MAX_VALUE);
    builder.pop();

    builder.pop();
    return builder;
  }

  public static ConfigSection[] entries() {
    var items = ConfigSection.builder(
      Component.translatable("screen.config.section.silicon.items.title")
    ).addEntries(
      ConfigEntry.booleanOf("redstoneChipset", Component.translatable("screen.config.entry.silicon.redstoneChipset.title"), Component.translatable("screen.config.entry.silicon.redstoneChipset.tooltip"), redstoneChipset),
      ConfigEntry.booleanOf("redstoneCrystal", Component.translatable("screen.config.entry.silicon.redstoneCrystal.title"), Component.translatable("screen.config.entry.silicon.redstoneCrystal.tooltip"), redstoneCrystal),
      ConfigEntry.booleanOf("redstoneBoard", Component.translatable("screen.config.entry.silicon.redstoneBoard.title"), Component.translatable("screen.config.entry.silicon.redstoneBoard.tooltip"), redstoneBoard),
      ConfigEntry.booleanOf("gateCopier", Component.translatable("screen.config.entry.silicon.gateCopier.title"), Component.translatable("screen.config.entry.silicon.gateCopier.tooltip"), gateCopier),
      ConfigEntry.booleanOf("pipeGate", Component.translatable("screen.config.entry.silicon.pipeGate.title"), Component.translatable("screen.config.entry.silicon.pipeGate.tooltip"), pipeGate),
      ConfigEntry.booleanOf("pipeLens", Component.translatable("screen.config.entry.silicon.pipeLens.title"), Component.translatable("screen.config.entry.silicon.pipeLens.tooltip"), pipeLens),
      ConfigEntry.booleanOf("pipeWire", Component.translatable("screen.config.entry.silicon.pipeWire.title"), Component.translatable("screen.config.entry.silicon.pipeWire.tooltip"), pipeWire)
    ).build();

    var blocks = ConfigSection.builder(
      Component.translatable("screen.config.section.silicon.blocks.title")
    ).addEntries(
      ConfigEntry.booleanOf("laserBlock", Component.translatable("screen.config.entry.silicon.laserBlock.title"), Component.translatable("screen.config.entry.silicon.laserBlock.tooltip"), laserBlock),
      ConfigEntry.booleanOf("laserTableBlock", Component.translatable("screen.config.entry.silicon.laserTableBlock.title"), Component.translatable("screen.config.entry.silicon.laserTableBlock.tooltip"), laserTableBlock),
      ConfigEntry.booleanOf("libraryBlock", Component.translatable("screen.config.entry.silicon.libraryBlock.title"), Component.translatable("screen.config.entry.silicon.libraryBlock.tooltip"), libraryBlock),
      ConfigEntry.booleanOf("zonePlan", Component.translatable("screen.config.entry.silicon.zonePlan.title"), Component.translatable("screen.config.entry.silicon.zonePlan.tooltip"), zonePlan)
    ).build();

    var power = ConfigSection.builder(
      Component.translatable("screen.config.section.silicon.power.title")
    ).addEntries(
      ConfigEntry.doubleOf(
        "chipsetCostMultiplier",
        Component.translatable("screen.config.entry.silicon.chipsetCostMultiplier.title"),
        Component.translatable("screen.config.entry.silicon.chipsetCostMultiplier.tooltip"),
        chipsetCostMultiplier,
        ConfigEntry.range(0.0, Double.MAX_VALUE)
      )
    ).build();

    return new ConfigSection[]{items, blocks, power};
  }
}
