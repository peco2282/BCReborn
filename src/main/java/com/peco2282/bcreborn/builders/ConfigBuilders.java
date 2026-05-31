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

import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;
import java.util.List;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ConfigBuilders {
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

  public static boolean isDropBrokenBlocks() { return dropBrokenBlocks.get(); }
  public static boolean isQuarryDoChunkLoading() { return quarryDoChunkLoading.get(); }
  public static boolean isQuarryOneTimeUse() { return quarryOneTimeUse.get(); }
  public static int getMiningDepth() { return miningDepth.get(); }
  public static String getServerDatabaseDirectory() { return serverDatabaseDirectory.get().replace("$MINECRAFT", new File(".").getAbsolutePath());  }
  public static String getClientDatabaseDirectory() { return clientDatabaseDirectory.get().replace("$MINECRAFT", new File(".").getAbsolutePath());  }
  public static List<? extends String> getExcludedBlocks() { return excludedBlocks.get(); }
  public static List<? extends String> getExcludedMods() { return excludedMods.get(); }

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
        .define("serverDatabaseDirectory", "$MINECRAFT/blueprints/server");
    clientDatabaseDirectory = builder.comment("Location for the client blueprint database (used by the Electronic Library).")
        .define("clientDatabaseDirectory", "$MINECRAFT/blueprints/client");
    excludedBlocks = builder.comment("Blocks that should be excluded from the builder.")
        .defineList("excludedBlocks", List.of(), e -> e instanceof String);
    excludedMods = builder.comment("Mods that should be excluded from the builder.")
        .defineList("excludedMods", List.of(), e -> e instanceof String);
    builder.pop();

    builder.pop();
    return builder;
  }
}
