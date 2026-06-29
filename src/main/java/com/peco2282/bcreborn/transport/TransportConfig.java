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
package com.peco2282.bcreborn.transport;

import com.peco2282.bcreborn.common.config.ConfigEntry;
import com.peco2282.bcreborn.common.config.ConfigSection;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class TransportConfig {
  // pipes - general
  private static ForgeConfigSpec.IntValue baseFluidRate;
  private static ForgeConfigSpec.DoubleValue pipeHardness;
  private static ForgeConfigSpec.BooleanValue facadeBlacklistAsWhitelist;
  private static ForgeConfigSpec.BooleanValue facadeNoLaserRecipe;
  private static ForgeConfigSpec.BooleanValue facadeShowAllInCreative;
  private static ForgeConfigSpec.BooleanValue slimeballWaterproofRecipe;
  private static ForgeConfigSpec.ConfigValue<List<? extends String>> facadeBlacklist;
  private static ForgeConfigSpec.DoubleValue gateCostMultiplier;
  private static ForgeConfigSpec.BooleanValue kinesisPowerLossOnTravel;

  // pipes - item enable/disable
  private static ForgeConfigSpec.BooleanValue pipeItemsWood;
  private static ForgeConfigSpec.BooleanValue pipeItemsCobblestone;
  private static ForgeConfigSpec.BooleanValue pipeItemsStone;
  private static ForgeConfigSpec.BooleanValue pipeItemsIron;
  private static ForgeConfigSpec.BooleanValue pipeItemsGold;
  private static ForgeConfigSpec.BooleanValue pipeItemsDiamond;
  private static ForgeConfigSpec.BooleanValue pipeItemsEmerald;
  private static ForgeConfigSpec.BooleanValue pipeItemsObsidian;
  private static ForgeConfigSpec.BooleanValue pipeItemsSandstone;
  private static ForgeConfigSpec.BooleanValue pipeItemsVoid;
  private static ForgeConfigSpec.BooleanValue pipeItemsClay;
  private static ForgeConfigSpec.BooleanValue pipeItemsQuartz;
  private static ForgeConfigSpec.BooleanValue pipeItemsLapis;
  private static ForgeConfigSpec.BooleanValue pipeItemsDaizuli;
  private static ForgeConfigSpec.BooleanValue pipeItemsEmzuli;
  private static ForgeConfigSpec.BooleanValue pipeItemsStripes;

  // pipes - fluid enable/disable
  private static ForgeConfigSpec.BooleanValue pipeFluidsWood;
  private static ForgeConfigSpec.BooleanValue pipeFluidsCobblestone;
  private static ForgeConfigSpec.BooleanValue pipeFluidsStone;
  private static ForgeConfigSpec.BooleanValue pipeFluidsIron;
  private static ForgeConfigSpec.BooleanValue pipeFluidsGold;
  private static ForgeConfigSpec.BooleanValue pipeFluidsDiamond;
  private static ForgeConfigSpec.BooleanValue pipeFluidsEmerald;
  private static ForgeConfigSpec.BooleanValue pipeFluidsSandstone;
  private static ForgeConfigSpec.BooleanValue pipeFluidsVoid;
  private static ForgeConfigSpec.BooleanValue pipeFluidsClay;
  private static ForgeConfigSpec.BooleanValue pipeFluidsQuartz;

  // pipes - power enable/disable
  private static ForgeConfigSpec.BooleanValue pipePowerWood;
  private static ForgeConfigSpec.BooleanValue pipePowerCobblestone;
  private static ForgeConfigSpec.BooleanValue pipePowerStone;
  private static ForgeConfigSpec.BooleanValue pipePowerIron;
  private static ForgeConfigSpec.BooleanValue pipePowerGold;
  private static ForgeConfigSpec.BooleanValue pipePowerDiamond;
  private static ForgeConfigSpec.BooleanValue pipePowerEmerald;
  private static ForgeConfigSpec.BooleanValue pipePowerSandstone;
  private static ForgeConfigSpec.BooleanValue pipePowerQuartz;
  private static ForgeConfigSpec.BooleanValue pipeStructureCobblestone;

  public static int getBaseFluidRate() {
    return baseFluidRate.get();
  }

  public static double getPipeHardness() {
    return pipeHardness.get();
  }

  public static boolean isFacadeBlacklistAsWhitelist() {
    return facadeBlacklistAsWhitelist.get();
  }

  public static boolean isFacadeNoLaserRecipe() {
    return facadeNoLaserRecipe.get();
  }

  public static boolean isFacadeShowAllInCreative() {
    return facadeShowAllInCreative.get();
  }

  public static boolean isSlimeballWaterproofRecipe() {
    return slimeballWaterproofRecipe.get();
  }

  public static List<? extends String> getFacadeBlacklist() {
    return facadeBlacklist.get();
  }

  public static double getGateCostMultiplier() {
    return gateCostMultiplier.get();
  }

  public static boolean isKinesisPowerLossOnTravel() {
    return kinesisPowerLossOnTravel.get();
  }

  public static boolean isPipeItemsWood() {
    return pipeItemsWood.get();
  }

  public static boolean isPipeItemsCobblestone() {
    return pipeItemsCobblestone.get();
  }

  public static boolean isPipeItemsStone() {
    return pipeItemsStone.get();
  }

  public static boolean isPipeItemsIron() {
    return pipeItemsIron.get();
  }

  public static boolean isPipeItemsGold() {
    return pipeItemsGold.get();
  }

  public static boolean isPipeItemsDiamond() {
    return pipeItemsDiamond.get();
  }

  public static boolean isPipeItemsEmerald() {
    return pipeItemsEmerald.get();
  }

  public static boolean isPipeItemsObsidian() {
    return pipeItemsObsidian.get();
  }

  public static boolean isPipeItemsSandstone() {
    return pipeItemsSandstone.get();
  }

  public static boolean isPipeItemsVoid() {
    return pipeItemsVoid.get();
  }

  public static boolean isPipeItemsClay() {
    return pipeItemsClay.get();
  }

  public static boolean isPipeItemsQuartz() {
    return pipeItemsQuartz.get();
  }

  public static boolean isPipeItemsLapis() {
    return pipeItemsLapis.get();
  }

  public static boolean isPipeItemsDaizuli() {
    return pipeItemsDaizuli.get();
  }

  public static boolean isPipeItemsEmzuli() {
    return pipeItemsEmzuli.get();
  }

  public static boolean isPipeItemsStripes() {
    return pipeItemsStripes.get();
  }

  public static boolean isPipeFluidsWood() {
    return pipeFluidsWood.get();
  }

  public static boolean isPipeFluidsCobblestone() {
    return pipeFluidsCobblestone.get();
  }

  public static boolean isPipeFluidsStone() {
    return pipeFluidsStone.get();
  }

  public static boolean isPipeFluidsIron() {
    return pipeFluidsIron.get();
  }

  public static boolean isPipeFluidsGold() {
    return pipeFluidsGold.get();
  }

  public static boolean isPipeFluidsDiamond() {
    return pipeFluidsDiamond.get();
  }

  public static boolean isPipeFluidsEmerald() {
    return pipeFluidsEmerald.get();
  }

  public static boolean isPipeFluidsSandstone() {
    return pipeFluidsSandstone.get();
  }

  public static boolean isPipeFluidsVoid() {
    return pipeFluidsVoid.get();
  }

  public static boolean isPipeFluidsClay() {
    return pipeFluidsClay.get();
  }

  public static boolean isPipeFluidsQuartz() {
    return pipeFluidsQuartz.get();
  }

  public static boolean isPipePowerWood() {
    return pipePowerWood.get();
  }

  public static boolean isPipePowerCobblestone() {
    return pipePowerCobblestone.get();
  }

  public static boolean isPipePowerStone() {
    return pipePowerStone.get();
  }

  public static boolean isPipePowerIron() {
    return pipePowerIron.get();
  }

  public static boolean isPipePowerGold() {
    return pipePowerGold.get();
  }

  public static boolean isPipePowerDiamond() {
    return pipePowerDiamond.get();
  }

  public static boolean isPipePowerEmerald() {
    return pipePowerEmerald.get();
  }

  public static boolean isPipePowerSandstone() {
    return pipePowerSandstone.get();
  }

  public static boolean isPipePowerQuartz() {
    return pipePowerQuartz.get();
  }

  public static boolean isPipeStructureCobblestone() {
    return pipeStructureCobblestone.get();
  }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Transport settings").push("transport");

    builder.comment("Pipe settings").push("pipes");
    baseFluidRate = builder.comment("What should the base flow rate of a fluid pipe be?")
      .defineInRange("baseFluidRate", 10, 1, 40);
    pipeHardness = builder.comment("How hard to break should a pipe be?")
      .defineInRange("hardness", 0.25, 0.0, Double.MAX_VALUE);
    facadeBlacklistAsWhitelist = builder.comment("Should the blacklist be treated as a whitelist instead?")
      .define("facadeBlacklistAsWhitelist", false);
    facadeNoLaserRecipe = builder.comment("Should non-laser (crafting table) facade recipes be forced?")
      .define("facadeNoLaserRecipe", false);
    facadeShowAllInCreative = builder.comment("Should all BC facades be shown in Creative/NEI, or just a few carefully chosen ones?")
      .define("facadeShowAllInCreative", true);
    slimeballWaterproofRecipe = builder.comment("Should I enable an alternate Waterproof recipe, based on slimeballs?")
      .define("slimeballWaterproofRecipe", false);
    gateCostMultiplier = builder.comment("What should be the multiplier of all gate power costs?")
      .defineInRange("gateCostMultiplier", 1.0, 0.0, Double.MAX_VALUE);
    kinesisPowerLossOnTravel = builder.comment("Should kinesis pipes lose power over distance (think IC2 or BC pre-3.7)?")
      .define("kinesisPowerLossOnTravel", false);
    facadeBlacklist = builder.comment("What block types should be blacklisted from being a facade?")
      .defineList("facadeBlacklist", List.of(
        "minecraft:end_portal_frame",
        "minecraft:grass",
        "minecraft:leaves",
        "minecraft:lit_pumpkin",
        "minecraft:lit_redstone_lamp",
        "minecraft:mob_spawner",
        "minecraft:monster_egg",
        "minecraft:redstone_lamp",
        "minecraft:sponge"
      ), e -> e instanceof String);

    builder.comment("Item pipe enable/disable").push("items");
    pipeItemsWood = builder.define("PipeItemsWood", true);
    pipeItemsCobblestone = builder.define("PipeItemsCobblestone", true);
    pipeItemsStone = builder.define("PipeItemsStone", true);
    pipeItemsIron = builder.define("PipeItemsIron", true);
    pipeItemsGold = builder.define("PipeItemsGold", true);
    pipeItemsDiamond = builder.define("PipeItemsDiamond", true);
    pipeItemsEmerald = builder.define("PipeItemsEmerald", true);
    pipeItemsObsidian = builder.define("PipeItemsObsidian", true);
    pipeItemsSandstone = builder.define("PipeItemsSandstone", true);
    pipeItemsVoid = builder.define("PipeItemsVoid", true);
    pipeItemsClay = builder.define("PipeItemsClay", true);
    pipeItemsQuartz = builder.define("PipeItemsQuartz", true);
    pipeItemsLapis = builder.define("PipeItemsLapis", true);
    pipeItemsDaizuli = builder.define("PipeItemsDaizuli", true);
    pipeItemsEmzuli = builder.define("PipeItemsEmzuli", true);
    pipeItemsStripes = builder.define("PipeItemsStripes", true);
    builder.pop();

    builder.comment("Fluid pipe enable/disable").push("fluids");
    pipeFluidsWood = builder.define("PipeFluidsWood", true);
    pipeFluidsCobblestone = builder.define("PipeFluidsCobblestone", true);
    pipeFluidsStone = builder.define("PipeFluidsStone", true);
    pipeFluidsIron = builder.define("PipeFluidsIron", true);
    pipeFluidsGold = builder.define("PipeFluidsGold", true);
    pipeFluidsDiamond = builder.define("PipeFluidsDiamond", true);
    pipeFluidsEmerald = builder.define("PipeFluidsEmerald", true);
    pipeFluidsSandstone = builder.define("PipeFluidsSandstone", true);
    pipeFluidsVoid = builder.define("PipeFluidsVoid", true);
    pipeFluidsClay = builder.define("PipeFluidsClay", true);
    pipeFluidsQuartz = builder.define("PipeFluidsQuartz", true);
    builder.pop();

    builder.comment("Power pipe enable/disable").push("power");
    pipePowerWood = builder.define("PipePowerWood", true);
    pipePowerCobblestone = builder.define("PipePowerCobblestone", true);
    pipePowerStone = builder.define("PipePowerStone", true);
    pipePowerIron = builder.define("PipePowerIron", true);
    pipePowerGold = builder.define("PipePowerGold", true);
    pipePowerDiamond = builder.define("PipePowerDiamond", true);
    pipePowerEmerald = builder.define("PipePowerEmerald", true);
    pipePowerSandstone = builder.define("PipePowerSandstone", true);
    pipePowerQuartz = builder.define("PipePowerQuartz", true);
    pipeStructureCobblestone = builder.define("PipeStructureCobblestone", true);
    builder.pop();

    builder.pop(); // pipes
    builder.pop(); // transport
    return builder;
  }

  public static ConfigSection[] entries() {
    var general = ConfigSection.builder(
      Component.translatable("screen.config.section.transport.general.title")
    ).addEntries(
      ConfigEntry.integerOf(
        "baseFluidRate",
        Component.translatable("screen.config.entry.transport.baseFluidRate.title"),
        Component.translatable("screen.config.entry.transport.baseFluidRate.tooltip"),
        baseFluidRate,
        ConfigEntry.range(1, 40)
      ),
      ConfigEntry.doubleOf(
        "pipeHardness",
        Component.translatable("screen.config.entry.transport.pipeHardness.title"),
        Component.translatable("screen.config.entry.transport.pipeHardness.tooltip"),
        pipeHardness,
        ConfigEntry.range(0.0, Double.MAX_VALUE)
      ),
      ConfigEntry.booleanOf(
        "facadeBlacklistAsWhitelist",
        Component.translatable("screen.config.entry.transport.facadeBlacklistAsWhitelist.title"),
        Component.translatable("screen.config.entry.transport.facadeBlacklistAsWhitelist.tooltip"),
        facadeBlacklistAsWhitelist
      ),
      ConfigEntry.booleanOf(
        "facadeNoLaserRecipe",
        Component.translatable("screen.config.entry.transport.facadeNoLaserRecipe.title"),
        Component.translatable("screen.config.entry.transport.facadeNoLaserRecipe.tooltip"),
        facadeNoLaserRecipe
      ),
      ConfigEntry.booleanOf(
        "facadeShowAllInCreative",
        Component.translatable("screen.config.entry.transport.facadeShowAllInCreative.title"),
        Component.translatable("screen.config.entry.transport.facadeShowAllInCreative.tooltip"),
        facadeShowAllInCreative
      ),
      ConfigEntry.booleanOf(
        "slimeballWaterproofRecipe",
        Component.translatable("screen.config.entry.transport.slimeballWaterproofRecipe.title"),
        Component.translatable("screen.config.entry.transport.slimeballWaterproofRecipe.tooltip"),
        slimeballWaterproofRecipe
      ),
      ConfigEntry.doubleOf(
        "gateCostMultiplier",
        Component.translatable("screen.config.entry.transport.gateCostMultiplier.title"),
        Component.translatable("screen.config.entry.transport.gateCostMultiplier.tooltip"),
        gateCostMultiplier,
        ConfigEntry.range(0.0, Double.MAX_VALUE)
      ),
      ConfigEntry.booleanOf(
        "kinesisPowerLossOnTravel",
        Component.translatable("screen.config.entry.transport.kinesisPowerLossOnTravel.title"),
        Component.translatable("screen.config.entry.transport.kinesisPowerLossOnTravel.tooltip"),
        kinesisPowerLossOnTravel
      )
    ).build();

    var items = ConfigSection.builder(
      Component.translatable("screen.config.section.transport.items.title")
    ).addEntries(
      ConfigEntry.booleanOf("pipeItemsWood", Component.translatable("screen.config.entry.transport.pipeItemsWood.title"), Component.translatable("screen.config.entry.transport.pipeItemsWood.tooltip"), pipeItemsWood),
      ConfigEntry.booleanOf("pipeItemsCobblestone", Component.translatable("screen.config.entry.transport.pipeItemsCobblestone.title"), Component.translatable("screen.config.entry.transport.pipeItemsCobblestone.tooltip"), pipeItemsCobblestone),
      ConfigEntry.booleanOf("pipeItemsStone", Component.translatable("screen.config.entry.transport.pipeItemsStone.title"), Component.translatable("screen.config.entry.transport.pipeItemsStone.tooltip"), pipeItemsStone),
      ConfigEntry.booleanOf("pipeItemsIron", Component.translatable("screen.config.entry.transport.pipeItemsIron.title"), Component.translatable("screen.config.entry.transport.pipeItemsIron.tooltip"), pipeItemsIron),
      ConfigEntry.booleanOf("pipeItemsGold", Component.translatable("screen.config.entry.transport.pipeItemsGold.title"), Component.translatable("screen.config.entry.transport.pipeItemsGold.tooltip"), pipeItemsGold),
      ConfigEntry.booleanOf("pipeItemsDiamond", Component.translatable("screen.config.entry.transport.pipeItemsDiamond.title"), Component.translatable("screen.config.entry.transport.pipeItemsDiamond.tooltip"), pipeItemsDiamond),
      ConfigEntry.booleanOf("pipeItemsEmerald", Component.translatable("screen.config.entry.transport.pipeItemsEmerald.title"), Component.translatable("screen.config.entry.transport.pipeItemsEmerald.tooltip"), pipeItemsEmerald),
      ConfigEntry.booleanOf("pipeItemsObsidian", Component.translatable("screen.config.entry.transport.pipeItemsObsidian.title"), Component.translatable("screen.config.entry.transport.pipeItemsObsidian.tooltip"), pipeItemsObsidian),
      ConfigEntry.booleanOf("pipeItemsSandstone", Component.translatable("screen.config.entry.transport.pipeItemsSandstone.title"), Component.translatable("screen.config.entry.transport.pipeItemsSandstone.tooltip"), pipeItemsSandstone),
      ConfigEntry.booleanOf("pipeItemsVoid", Component.translatable("screen.config.entry.transport.pipeItemsVoid.title"), Component.translatable("screen.config.entry.transport.pipeItemsVoid.tooltip"), pipeItemsVoid),
      ConfigEntry.booleanOf("pipeItemsClay", Component.translatable("screen.config.entry.transport.pipeItemsClay.title"), Component.translatable("screen.config.entry.transport.pipeItemsClay.tooltip"), pipeItemsClay),
      ConfigEntry.booleanOf("pipeItemsQuartz", Component.translatable("screen.config.entry.transport.pipeItemsQuartz.title"), Component.translatable("screen.config.entry.transport.pipeItemsQuartz.tooltip"), pipeItemsQuartz),
      ConfigEntry.booleanOf("pipeItemsLapis", Component.translatable("screen.config.entry.transport.pipeItemsLapis.title"), Component.translatable("screen.config.entry.transport.pipeItemsLapis.tooltip"), pipeItemsLapis),
      ConfigEntry.booleanOf("pipeItemsDaizuli", Component.translatable("screen.config.entry.transport.pipeItemsDaizuli.title"), Component.translatable("screen.config.entry.transport.pipeItemsDaizuli.tooltip"), pipeItemsDaizuli),
      ConfigEntry.booleanOf("pipeItemsEmzuli", Component.translatable("screen.config.entry.transport.pipeItemsEmzuli.title"), Component.translatable("screen.config.entry.transport.pipeItemsEmzuli.tooltip"), pipeItemsEmzuli),
      ConfigEntry.booleanOf("pipeItemsStripes", Component.translatable("screen.config.entry.transport.pipeItemsStripes.title"), Component.translatable("screen.config.entry.transport.pipeItemsStripes.tooltip"), pipeItemsStripes)
    ).build();

    var fluids = ConfigSection.builder(
      Component.translatable("screen.config.section.transport.fluids.title")
    ).addEntries(
      ConfigEntry.booleanOf("pipeFluidsWood", Component.translatable("screen.config.entry.transport.pipeFluidsWood.title"), Component.translatable("screen.config.entry.transport.pipeFluidsWood.tooltip"), pipeFluidsWood),
      ConfigEntry.booleanOf("pipeFluidsCobblestone", Component.translatable("screen.config.entry.transport.pipeFluidsCobblestone.title"), Component.translatable("screen.config.entry.transport.pipeFluidsCobblestone.tooltip"), pipeFluidsCobblestone),
      ConfigEntry.booleanOf("pipeFluidsStone", Component.translatable("screen.config.entry.transport.pipeFluidsStone.title"), Component.translatable("screen.config.entry.transport.pipeFluidsStone.tooltip"), pipeFluidsStone),
      ConfigEntry.booleanOf("pipeFluidsIron", Component.translatable("screen.config.entry.transport.pipeFluidsIron.title"), Component.translatable("screen.config.entry.transport.pipeFluidsIron.tooltip"), pipeFluidsIron),
      ConfigEntry.booleanOf("pipeFluidsGold", Component.translatable("screen.config.entry.transport.pipeFluidsGold.title"), Component.translatable("screen.config.entry.transport.pipeFluidsGold.tooltip"), pipeFluidsGold),
      ConfigEntry.booleanOf("pipeFluidsDiamond", Component.translatable("screen.config.entry.transport.pipeFluidsDiamond.title"), Component.translatable("screen.config.entry.transport.pipeFluidsDiamond.tooltip"), pipeFluidsDiamond),
      ConfigEntry.booleanOf("pipeFluidsEmerald", Component.translatable("screen.config.entry.transport.pipeFluidsEmerald.title"), Component.translatable("screen.config.entry.transport.pipeFluidsEmerald.tooltip"), pipeFluidsEmerald),
      ConfigEntry.booleanOf("pipeFluidsSandstone", Component.translatable("screen.config.entry.transport.pipeFluidsSandstone.title"), Component.translatable("screen.config.entry.transport.pipeFluidsSandstone.tooltip"), pipeFluidsSandstone),
      ConfigEntry.booleanOf("pipeFluidsVoid", Component.translatable("screen.config.entry.transport.pipeFluidsVoid.title"), Component.translatable("screen.config.entry.transport.pipeFluidsVoid.tooltip"), pipeFluidsVoid),
      ConfigEntry.booleanOf("pipeFluidsClay", Component.translatable("screen.config.entry.transport.pipeFluidsClay.title"), Component.translatable("screen.config.entry.transport.pipeFluidsClay.tooltip"), pipeFluidsClay),
      ConfigEntry.booleanOf("pipeFluidsQuartz", Component.translatable("screen.config.entry.transport.pipeFluidsQuartz.title"), Component.translatable("screen.config.entry.transport.pipeFluidsQuartz.tooltip"), pipeFluidsQuartz)
    ).build();

    var power = ConfigSection.builder(
      Component.translatable("screen.config.section.transport.power.title")
    ).addEntries(
      ConfigEntry.booleanOf("pipePowerWood", Component.translatable("screen.config.entry.transport.pipePowerWood.title"), Component.translatable("screen.config.entry.transport.pipePowerWood.tooltip"), pipePowerWood),
      ConfigEntry.booleanOf("pipePowerCobblestone", Component.translatable("screen.config.entry.transport.pipePowerCobblestone.title"), Component.translatable("screen.config.entry.transport.pipePowerCobblestone.tooltip"), pipePowerCobblestone),
      ConfigEntry.booleanOf("pipePowerStone", Component.translatable("screen.config.entry.transport.pipePowerStone.title"), Component.translatable("screen.config.entry.transport.pipePowerStone.tooltip"), pipePowerStone),
      ConfigEntry.booleanOf("pipePowerIron", Component.translatable("screen.config.entry.transport.pipePowerIron.title"), Component.translatable("screen.config.entry.transport.pipePowerIron.tooltip"), pipePowerIron),
      ConfigEntry.booleanOf("pipePowerGold", Component.translatable("screen.config.entry.transport.pipePowerGold.title"), Component.translatable("screen.config.entry.transport.pipePowerGold.tooltip"), pipePowerGold),
      ConfigEntry.booleanOf("pipePowerDiamond", Component.translatable("screen.config.entry.transport.pipePowerDiamond.title"), Component.translatable("screen.config.entry.transport.pipePowerDiamond.tooltip"), pipePowerDiamond),
      ConfigEntry.booleanOf("pipePowerEmerald", Component.translatable("screen.config.entry.transport.pipePowerEmerald.title"), Component.translatable("screen.config.entry.transport.pipePowerEmerald.tooltip"), pipePowerEmerald),
      ConfigEntry.booleanOf("pipePowerSandstone", Component.translatable("screen.config.entry.transport.pipePowerSandstone.title"), Component.translatable("screen.config.entry.transport.pipePowerSandstone.tooltip"), pipePowerSandstone),
      ConfigEntry.booleanOf("pipePowerQuartz", Component.translatable("screen.config.entry.transport.pipePowerQuartz.title"), Component.translatable("screen.config.entry.transport.pipePowerQuartz.tooltip"), pipePowerQuartz),
      ConfigEntry.booleanOf("pipeStructureCobblestone", Component.translatable("screen.config.entry.transport.pipeStructureCobblestone.title"), Component.translatable("screen.config.entry.transport.pipeStructureCobblestone.tooltip"), pipeStructureCobblestone)
    ).build();

    return new ConfigSection[]{general, items, fluids, power};
  }
}
