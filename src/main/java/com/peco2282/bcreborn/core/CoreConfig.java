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
package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.common.config.ConfigEntry;
import com.peco2282.bcreborn.common.config.ConfigSection;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

public class CoreConfig {
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
  private static ForgeConfigSpec.DoubleValue miningUsageMultiplier;

  public static int getItemLifespan() {
    return itemLifespan.get();
  }

  public static int getMarkerRange() {
    return markerRange.get();
  }

  public static int getBuilderMaxIterationsPerItemFactor() {
    return builderMaxIterationsPerItemFactor.get();
  }

  public static boolean isCanEnginesExplode() {
    return canEnginesExplode.get();
  }

  public static boolean isUseServerDataOnClient() {
    return useServerDataOnClient.get();
  }

  public static boolean isUpdateCheck() {
    return updateCheck.get();
  }

  public static boolean isMiningBreaksPlayerProtectedBlocks() {
    return miningBreaksPlayerProtectedBlocks.get();
  }

  public static int getUpdateFactor() {
    return updateFactor.get();
  }

  public static int getLongUpdateFactor() {
    return longUpdateFactor.get();
  }

  public static double getMiningUsageMultiplier() {
    return miningUsageMultiplier.get();
  }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Core settings").push("core");

    builder.comment("General settings").push("general");
    itemLifespan = builder.comment("How long, in seconds, should items stay on the ground? (Vanilla = 300, default = 60)")
      .defineInRange("itemLifespan", 1200, 5, Integer.MAX_VALUE);
    markerRange = builder.comment("Set the maximum marker range.")
      .defineInRange("markerRange", 64, 8, 64);
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
    miningUsageMultiplier = builder.comment("What should the multiplier of all mining-related power usage be?")
      .defineInRange("miningUsageMultiplier", 1.0, 0.0, Double.MAX_VALUE);
    builder.pop();

    builder.pop();
    return builder;
  }

  public static ConfigSection[] entries() {
    var general = ConfigSection.builder(
      Component.translatable("screen.config.section.core.general.title")
    ).addEntries(
      ConfigEntry.integerOf(
        "itemLifespan",
        Component.translatable("screen.config.entry.core.itemLifespan.title"),
        Component.translatable("screen.config.entry.core.itemLifespan.tooltip"),
        itemLifespan,
        ConfigEntry.range(5, Integer.MAX_VALUE)
      ),
      ConfigEntry.integerOf(
        "markerRange",
        Component.translatable("screen.config.entry.core.markerRange.title"),
        Component.translatable("screen.config.entry.core.markerRange.tooltip"),
        markerRange,
        ConfigEntry.range(8, 64)
      ),
      ConfigEntry.integerOf(
        "builderMaxIterationsPerItemFactor",
        Component.translatable("screen.config.entry.core.builderMaxIterationsPerItemFactor.title"),
        Component.translatable("screen.config.entry.core.builderMaxIterationsPerItemFactor.tooltip"),
        builderMaxIterationsPerItemFactor,
        ConfigEntry.range(1, Integer.MAX_VALUE)
      ),
      ConfigEntry.booleanOf(
        "canEnginesExplode",
        Component.translatable("screen.config.entry.core.canEnginesExplode.title"),
        Component.translatable("screen.config.entry.core.canEnginesExplode.tooltip"),
        canEnginesExplode
      ),
      ConfigEntry.booleanOf(
        "useServerDataOnClient",
        Component.translatable("screen.config.entry.core.useServerDataOnClient.title"),
        Component.translatable("screen.config.entry.core.useServerDataOnClient.tooltip"),
        useServerDataOnClient
      ),
      ConfigEntry.booleanOf(
        "updateCheck",
        Component.translatable("screen.config.entry.core.updateCheck.title"),
        Component.translatable("screen.config.entry.core.updateCheck.tooltip"),
        updateCheck
      ),
      ConfigEntry.booleanOf(
        "miningBreaksPlayerProtectedBlocks",
        Component.translatable("screen.config.entry.core.miningBreaksPlayerProtectedBlocks.title"),
        Component.translatable("screen.config.entry.core.miningBreaksPlayerProtectedBlocks.tooltip"),
        miningBreaksPlayerProtectedBlocks
      )
    ).build();

    var network = ConfigSection.builder(
      Component.translatable("screen.config.section.core.network.title")
    ).addEntries(
      ConfigEntry.integerOf(
        "updateFactor",
        Component.translatable("screen.config.entry.core.updateFactor.title"),
        Component.translatable("screen.config.entry.core.updateFactor.tooltip"),
        updateFactor,
        ConfigEntry.range(1, Integer.MAX_VALUE)
      ),
      ConfigEntry.integerOf(
        "longUpdateFactor",
        Component.translatable("screen.config.entry.core.longUpdateFactor.title"),
        Component.translatable("screen.config.entry.core.longUpdateFactor.tooltip"),
        longUpdateFactor,
        ConfigEntry.range(1, Integer.MAX_VALUE)
      )
    ).build();

    var power = ConfigSection.builder(
      Component.translatable("screen.config.section.core.power.title")
    ).addEntries(
      ConfigEntry.doubleOf(
        "miningUsageMultiplier",
        Component.translatable("screen.config.entry.core.miningUsageMultiplier.title"),
        Component.translatable("screen.config.entry.core.miningUsageMultiplier.tooltip"),
        miningUsageMultiplier,
        ConfigEntry.range(0.0, Double.MAX_VALUE)
      )
    ).build();

    return new ConfigSection[]{general, network, power};
  }
}
