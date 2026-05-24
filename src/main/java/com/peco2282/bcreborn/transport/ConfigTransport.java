package com.peco2282.bcreborn.transport;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ConfigTransport {
  // pipes - general
  private static ForgeConfigSpec.IntValue baseFluidRate;
  private static ForgeConfigSpec.DoubleValue pipeHardness;
  private static ForgeConfigSpec.BooleanValue facadeBlacklistAsWhitelist;
  private static ForgeConfigSpec.BooleanValue facadeNoLaserRecipe;
  private static ForgeConfigSpec.BooleanValue facadeShowAllInCreative;
  private static ForgeConfigSpec.BooleanValue slimeballWaterproofRecipe;
  private static ForgeConfigSpec.ConfigValue<List<? extends String>> facadeBlacklist;

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

  public static int getBaseFluidRate() { return baseFluidRate.get(); }
  public static double getPipeHardness() { return pipeHardness.get(); }
  public static boolean isFacadeBlacklistAsWhitelist() { return facadeBlacklistAsWhitelist.get(); }
  public static boolean isFacadeNoLaserRecipe() { return facadeNoLaserRecipe.get(); }
  public static boolean isFacadeShowAllInCreative() { return facadeShowAllInCreative.get(); }
  public static boolean isSlimeballWaterproofRecipe() { return slimeballWaterproofRecipe.get(); }
  public static List<? extends String> getFacadeBlacklist() { return facadeBlacklist.get(); }

  public static boolean isPipeItemsWood() { return pipeItemsWood.get(); }
  public static boolean isPipeItemsCobblestone() { return pipeItemsCobblestone.get(); }
  public static boolean isPipeItemsStone() { return pipeItemsStone.get(); }
  public static boolean isPipeItemsIron() { return pipeItemsIron.get(); }
  public static boolean isPipeItemsGold() { return pipeItemsGold.get(); }
  public static boolean isPipeItemsDiamond() { return pipeItemsDiamond.get(); }
  public static boolean isPipeItemsEmerald() { return pipeItemsEmerald.get(); }
  public static boolean isPipeItemsObsidian() { return pipeItemsObsidian.get(); }
  public static boolean isPipeItemsSandstone() { return pipeItemsSandstone.get(); }
  public static boolean isPipeItemsVoid() { return pipeItemsVoid.get(); }
  public static boolean isPipeItemsClay() { return pipeItemsClay.get(); }
  public static boolean isPipeItemsQuartz() { return pipeItemsQuartz.get(); }
  public static boolean isPipeItemsLapis() { return pipeItemsLapis.get(); }
  public static boolean isPipeItemsDaizuli() { return pipeItemsDaizuli.get(); }
  public static boolean isPipeItemsEmzuli() { return pipeItemsEmzuli.get(); }
  public static boolean isPipeItemsStripes() { return pipeItemsStripes.get(); }

  public static boolean isPipeFluidsWood() { return pipeFluidsWood.get(); }
  public static boolean isPipeFluidsCobblestone() { return pipeFluidsCobblestone.get(); }
  public static boolean isPipeFluidsStone() { return pipeFluidsStone.get(); }
  public static boolean isPipeFluidsIron() { return pipeFluidsIron.get(); }
  public static boolean isPipeFluidsGold() { return pipeFluidsGold.get(); }
  public static boolean isPipeFluidsDiamond() { return pipeFluidsDiamond.get(); }
  public static boolean isPipeFluidsEmerald() { return pipeFluidsEmerald.get(); }
  public static boolean isPipeFluidsSandstone() { return pipeFluidsSandstone.get(); }
  public static boolean isPipeFluidsVoid() { return pipeFluidsVoid.get(); }
  public static boolean isPipeFluidsClay() { return pipeFluidsClay.get(); }
  public static boolean isPipeFluidsQuartz() { return pipeFluidsQuartz.get(); }

  public static boolean isPipePowerWood() { return pipePowerWood.get(); }
  public static boolean isPipePowerCobblestone() { return pipePowerCobblestone.get(); }
  public static boolean isPipePowerStone() { return pipePowerStone.get(); }
  public static boolean isPipePowerIron() { return pipePowerIron.get(); }
  public static boolean isPipePowerGold() { return pipePowerGold.get(); }
  public static boolean isPipePowerDiamond() { return pipePowerDiamond.get(); }
  public static boolean isPipePowerEmerald() { return pipePowerEmerald.get(); }
  public static boolean isPipePowerSandstone() { return pipePowerSandstone.get(); }
  public static boolean isPipePowerQuartz() { return pipePowerQuartz.get(); }
  public static boolean isPipeStructureCobblestone() { return pipeStructureCobblestone.get(); }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Transport settings").push("transport");

    builder.comment("Pipe settings").push("pipes");
    baseFluidRate = builder.comment("What should the base flow rate of a fluid pipe be?")
        .defineInRange("baseFluidRate", 10, 1, Integer.MAX_VALUE);
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
}
