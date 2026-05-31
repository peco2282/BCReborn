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
package com.peco2282.bcreborn.robotics;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ConfigRobotics {
  // items
  private static ForgeConfigSpec.BooleanValue robot;
  private static ForgeConfigSpec.BooleanValue robotStation;

  // blocks
  private static ForgeConfigSpec.BooleanValue architectBlock;
  private static ForgeConfigSpec.BooleanValue constructionMarkerBlock;

  // boards blacklist
  private static ForgeConfigSpec.ConfigValue<List<? extends String>> boardsBlacklist;

  public static boolean isRobot() { return robot.get(); }
  public static boolean isRobotStation() { return robotStation.get(); }
  public static boolean isArchitectBlock() { return architectBlock.get(); }
  public static boolean isConstructionMarkerBlock() { return constructionMarkerBlock.get(); }
  public static List<? extends String> getBoardsBlacklist() { return boardsBlacklist.get(); }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Robotics settings").push("robotics");

    builder.comment("Robotics item enable/disable").push("items");
    robot = builder.define("robot", true);
    robotStation = builder.define("robotStation", true);
    builder.pop();

    builder.comment("Robotics block enable/disable").push("blocks");
    architectBlock = builder.define("architectBlock", true);
    constructionMarkerBlock = builder.define("constructionMarkerBlock", true);
    builder.pop();

    boardsBlacklist = builder.comment("Blacklisted robot boards")
        .defineList("boards.blacklist", List.of(), e -> e instanceof String);

    builder.pop();
    return builder;
  }
}
