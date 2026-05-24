package com.peco2282.bcreborn.core;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ConfigCore {
  // general
  private static ForgeConfigSpec.IntValue itemLifespan;
  private static ForgeConfigSpec.IntValue markerRange;
  private static ForgeConfigSpec.IntValue builderMaxIterationsPerItemFactor;
  private static ForgeConfigSpec.BooleanValue canEnginesExplode;
  private static ForgeConfigSpec.BooleanValue useServerDataOnClient;
  private static ForgeConfigSpec.BooleanValue updateCheck;
  private static ForgeConfigSpec.BooleanValue miningBreaksPlayerProtectedBlocks;

  // network
  private static ForgeConfigSpec.IntValue updateFactor;
  private static ForgeConfigSpec.IntValue longUpdateFactor;

  // power
  private static ForgeConfigSpec.DoubleValue chipsetCostMultiplier;
  private static ForgeConfigSpec.DoubleValue gateCostMultiplier;
  private static ForgeConfigSpec.DoubleValue miningUsageMultiplier;

  public static int getItemLifespan() { return itemLifespan.get(); }
  public static int getMarkerRange() { return markerRange.get(); }
  public static int getBuilderMaxIterationsPerItemFactor() { return builderMaxIterationsPerItemFactor.get(); }
  public static boolean isCanEnginesExplode() { return canEnginesExplode.get(); }
  public static boolean isUseServerDataOnClient() { return useServerDataOnClient.get(); }
  public static boolean isUpdateCheck() { return updateCheck.get(); }
  public static boolean isMiningBreaksPlayerProtectedBlocks() { return miningBreaksPlayerProtectedBlocks.get(); }
  public static int getUpdateFactor() { return updateFactor.get(); }
  public static int getLongUpdateFactor() { return longUpdateFactor.get(); }
  public static double getChipsetCostMultiplier() { return chipsetCostMultiplier.get(); }
  public static double getGateCostMultiplier() { return gateCostMultiplier.get(); }
  public static double getMiningUsageMultiplier() { return miningUsageMultiplier.get(); }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Core settings").push("core");

    builder.comment("General settings").push("general");
    itemLifespan = builder.comment("How long, in seconds, should items stay on the ground? (Vanilla = 300, default = 60)")
        .defineInRange("itemLifespan", 1200, 1, Integer.MAX_VALUE);
    markerRange = builder.comment("Set the maximum marker range.")
        .defineInRange("markerRange", 64, 1, 256);
    builderMaxIterationsPerItemFactor = builder.comment("Lower this number if BuildCraft builders/fillers are causing TPS lag. Raise it if you think they are being too slow.")
        .defineInRange("builderMaxIterationsPerItemFactor", 1024, 1, Integer.MAX_VALUE);
    canEnginesExplode = builder.comment("Should engines explode upon overheat?")
        .define("canEnginesExplode", false);
    useServerDataOnClient = builder.comment("Allows BCReborn to use the integrated server's data on the client on singleplayer worlds. Disable if you're getting the odd crash caused by it.")
        .define("useServerDataOnClient", true);
    updateCheck = builder.comment("Should I check the BCReborn version on startup?")
        .define("updateCheck", true);
    miningBreaksPlayerProtectedBlocks = builder.comment("Should BCReborn miners be allowed to break blocks using player-specific protection?")
        .define("miningBreaksPlayerProtectedBlocks", false);
    builder.pop();

    builder.comment("Network settings").push("network");
    updateFactor = builder.comment("How often, in ticks, should network update packets be sent? Increasing this might help network performance.")
        .defineInRange("updateFactor", 10, 1, Integer.MAX_VALUE);
    longUpdateFactor = builder.comment("How often, in ticks, should full network sync packets be sent? Increasing this might help network performance.")
        .defineInRange("longUpdateFactor", 40, 1, Integer.MAX_VALUE);
    builder.pop();

    builder.comment("Power settings").push("power");
    chipsetCostMultiplier = builder.comment("The cost multiplier for Chipsets")
        .defineInRange("chipsetCostMultiplier", 1.0, 0.0, Double.MAX_VALUE);
    gateCostMultiplier = builder.comment("What should be the multiplier of all gate power costs?")
        .defineInRange("gateCostMultiplier", 1.0, 0.0, Double.MAX_VALUE);
    miningUsageMultiplier = builder.comment("What should the multiplier of all mining-related power usage be?")
        .defineInRange("miningUsageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
    builder.pop();

    builder.pop();
    return builder;
  }
}
