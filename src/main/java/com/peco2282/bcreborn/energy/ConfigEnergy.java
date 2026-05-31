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
package com.peco2282.bcreborn.energy;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigEnergy {
  // fuel
  private static ForgeConfigSpec.DoubleValue fuelFuelCombustion;
  private static ForgeConfigSpec.IntValue fuelFuelCombustionEnergyOutput;
  private static ForgeConfigSpec.DoubleValue fuelOilCombustion;
  private static ForgeConfigSpec.IntValue fuelOilCombustionEnergyOutput;

  // oil
  private static ForgeConfigSpec.BooleanValue oilCanBurn;
  private static ForgeConfigSpec.BooleanValue oilIsDense;

  // pump
  private static ForgeConfigSpec.BooleanValue pumpsConsumeWater;
  private static ForgeConfigSpec.BooleanValue pumpsNeedRealPower;
  private static ForgeConfigSpec.ConfigValue<String> pumpDimensionControl;

  // worldgen
  private static ForgeConfigSpec.BooleanValue worldgenEnable;
  private static ForgeConfigSpec.BooleanValue generateWaterSprings;
  private static ForgeConfigSpec.DoubleValue oilWellGenerationRate;
  private static ForgeConfigSpec.BooleanValue spawnOilSprings;

  public static double getFuelFuelCombustion() { return fuelFuelCombustion.get(); }
  public static int getFuelFuelCombustionEnergyOutput() { return fuelFuelCombustionEnergyOutput.get(); }
  public static double getFuelOilCombustion() { return fuelOilCombustion.get(); }
  public static int getFuelOilCombustionEnergyOutput() { return fuelOilCombustionEnergyOutput.get(); }
  public static boolean isOilCanBurn() { return oilCanBurn.get(); }
  public static boolean isOilIsDense() { return oilIsDense.get(); }
  public static boolean isPumpsConsumeWater() { return pumpsConsumeWater.get(); }
  public static boolean isPumpsNeedRealPower() { return pumpsNeedRealPower.get(); }
  public static String getPumpDimensionControl() { return pumpDimensionControl.get(); }
  public static boolean isWorldgenEnable() { return worldgenEnable.get(); }
  public static boolean isGenerateWaterSprings() { return generateWaterSprings.get(); }
  public static double getOilWellGenerationRate() { return oilWellGenerationRate.get(); }
  public static boolean isSpawnOilSprings() { return spawnOilSprings.get(); }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Energy settings").push("energy");

    builder.comment("Fuel settings").push("fuel");
    fuelFuelCombustion = builder.comment("adjust energy value of Fuel in Combustion Engines")
        .defineInRange("fuel.combustion.energyValue", 1.0, 0.0, Double.MAX_VALUE);
    fuelFuelCombustionEnergyOutput = builder.comment("adjust output energy by Fuel in Combustion Engines")
        .defineInRange("fuel.combustion.energyOutput", 60, 1, Integer.MAX_VALUE);
    fuelOilCombustion = builder.comment("adjust energy value of Oil in Combustion Engines")
        .defineInRange("oil.combustion.energyValue", 1.0, 0.0, Double.MAX_VALUE);
    fuelOilCombustionEnergyOutput = builder.comment("adjust output energy by Oil in Combustion Engines")
        .defineInRange("oil.combustion.energyOutput", 30, 1, Integer.MAX_VALUE);
    builder.pop();

    builder.comment("Oil settings").push("oil");
    oilCanBurn = builder.comment("Should oil burn when lit on fire?")
        .define("oilCanBurn", true);
    oilIsDense = builder.comment("Should oil be dense and drag entities down?")
        .define("oilIsDense", true);
    builder.pop();

    builder.comment("Pump settings").push("pump");
    pumpsConsumeWater = builder.comment("Should pumps consume water? Enabling this might cause performance issues!")
        .define("pumpsConsumeWater", false);
    pumpsNeedRealPower = builder.comment("Do pumps need real (non-redstone) power?")
        .define("pumpsNeedRealPower", false);
    pumpDimensionControl = builder.comment(
            "Allows admins to whitelist or blacklist pumping of specific fluids in specific dimensions.",
            "Eg. \"-/-1/Lava\" will disable lava in the nether. \"-/*/Lava\" will disable lava in any dimension. \"+/0/*\" will enable any fluid in the overworld.",
            "Entries are comma separated, banned fluids have precedence over allowed ones.")
        .define("pumpDimensionControl", "+/*/*,+/-1/lava");
    builder.pop();

    builder.comment("World generation settings").push("worldgen");
    worldgenEnable = builder.comment("Should BCReborn generate anything in the world?")
        .define("enable", true);
    generateWaterSprings = builder.comment("Should BCReborn generate water springs?")
        .define("generateWaterSprings", true);
    oilWellGenerationRate = builder.comment("How high should be the probability of an oil well generating?")
        .defineInRange("oilWellGenerationRate", 1.0, 0.0, Double.MAX_VALUE);
    spawnOilSprings = builder.comment("Should I spawn oil springs?")
        .define("spawnOilSprings", true);
    builder.pop();

    builder.pop();
    return builder;
  }
}
