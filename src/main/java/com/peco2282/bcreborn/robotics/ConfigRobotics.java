package com.peco2282.bcreborn.robotics;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigRobotics {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Robotics settings").push("robotics");
    builder.pop();
    return builder;
  }
}
