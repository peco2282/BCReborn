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
package com.peco2282.bcreborn.builders;

import com.peco2282.bcreborn.common.config.ConfigEntry;
import com.peco2282.bcreborn.common.config.ConfigSection;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;
import java.util.List;

public class BuildersConfig {
  // builders
  private static ForgeConfigSpec.BooleanValue dropBrokenBlocks;

  // quarry
  private static ForgeConfigSpec.BooleanValue quarryDoChunkLoading;
  private static ForgeConfigSpec.BooleanValue quarryOneTimeUse;
  private static ForgeConfigSpec.IntValue miningDepth;

  // blueprints
  private static ForgeConfigSpec.ConfigValue<String> serverDatabaseDirectory;
  private static ForgeConfigSpec.ConfigValue<String> clientDatabaseDirectory;
  private static ForgeConfigSpec.ConfigValue<List<? extends String>> excludedBlocks;
  private static ForgeConfigSpec.ConfigValue<List<? extends String>> excludedMods;

  public static boolean isDropBrokenBlocks() {
    return dropBrokenBlocks.get();
  }

  public static boolean isQuarryDoChunkLoading() {
    return quarryDoChunkLoading.get();
  }

  public static boolean isQuarryOneTimeUse() {
    return quarryOneTimeUse.get();
  }

  public static int getMiningDepth() {
    return miningDepth.get();
  }

  public static String getServerDatabaseDirectory() {
    return serverDatabaseDirectory.get().replace("$MINECRAFT", new File(".").getAbsolutePath());
  }

  public static String getClientDatabaseDirectory() {
    return clientDatabaseDirectory.get().replace("$MINECRAFT", new File(".").getAbsolutePath());
  }

  public static List<? extends String> getExcludedBlocks() {
    return excludedBlocks.get();
  }

  public static List<? extends String> getExcludedMods() {
    return excludedMods.get();
  }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Builders settings").push("builders");

    dropBrokenBlocks = builder.comment("Should the builder and filler drop the cleared blocks?")
      .define("dropBrokenBlocks", false);

    builder.comment("Quarry settings").push("quarry");
    quarryDoChunkLoading = builder.comment("Should the quarry keep the chunks it is working on loaded?")
      .define("doChunkLoading", true);
    quarryOneTimeUse = builder.comment("Should the quarry only be usable once after placing?")
      .define("oneTimeUse", false);
    miningDepth = builder.comment("Maximum mining depth for the mining well.")
      .defineInRange("miningDepth", 256, 1, 512);
    builder.pop();

    builder.comment("Blueprint settings").push("blueprints");
    serverDatabaseDirectory = builder.comment("Location for the server blueprint database (used by the Electronic Library).")
      .define("serverDatabaseDirectory", "$MINECRAFT/config/buildcraft/blueprints/server");
    clientDatabaseDirectory = builder.comment("Location for the client blueprint database (used by the Electronic Library).")
      .define("clientDatabaseDirectory", "$MINECRAFT/blueprints");
    excludedBlocks = builder.comment("Blocks that should be excluded from the builder.")
      .defineList("excludedBlocks", List.of(), e -> e instanceof String);
    excludedMods = builder.comment("Mods that should be excluded from the builder.")
      .defineList("excludedMods", List.of(), e -> e instanceof String);
    builder.pop();

    builder.pop();
    return builder;
  }

  public static ConfigSection[] entries() {
    var general = ConfigSection.builder(
      Component.translatable("screen.config.section.builders.general.title")
    ).addEntries(
      ConfigEntry.booleanOf(
        "dropBrokenBlocks",
        Component.translatable("screen.config.entry.builders.dropBrokenBlocks.title"),
        Component.translatable("screen.config.entry.builders.dropBrokenBlocks.tooltip"),
        dropBrokenBlocks
      )
    ).build();

    var quarry = ConfigSection.builder(
      Component.translatable("screen.config.section.builders.quarry.title")
    ).addEntries(
      ConfigEntry.booleanOf(
        "quarryDoChunkLoading",
        Component.translatable("screen.config.entry.builders.quarryDoChunkLoading.title"),
        Component.translatable("screen.config.entry.builders.quarryDoChunkLoading.tooltip"),
        quarryDoChunkLoading
      ),
      ConfigEntry.booleanOf(
        "quarryOneTimeUse",
        Component.translatable("screen.config.entry.builders.quarryOneTimeUse.title"),
        Component.translatable("screen.config.entry.builders.quarryOneTimeUse.tooltip"),
        quarryOneTimeUse
      ),
      ConfigEntry.integerOf(
        "miningDepth",
        Component.translatable("screen.config.entry.builders.miningDepth.title"),
        Component.translatable("screen.config.entry.builders.miningDepth.tooltip"),
        miningDepth,
        ConfigEntry.range(1, 512)
      )
    ).build();

    var blueprints = ConfigSection.builder(
      Component.translatable("screen.config.section.builders.blueprints.title")
    ).addEntries(
      ConfigEntry.stringOf(
        "serverDatabaseDirectory",
        Component.translatable("screen.config.entry.builders.serverDatabaseDirectory.title"),
        Component.translatable("screen.config.entry.builders.serverDatabaseDirectory.tooltip"),
        serverDatabaseDirectory,
        t -> true
      ),
      ConfigEntry.stringOf(
        "clientDatabaseDirectory",
        Component.translatable("screen.config.entry.builders.clientDatabaseDirectory.title"),
        Component.translatable("screen.config.entry.builders.clientDatabaseDirectory.tooltip"),
        clientDatabaseDirectory,
        t -> true
      )
    ).build();

    return new ConfigSection[] { general, quarry, blueprints };
  }
}
