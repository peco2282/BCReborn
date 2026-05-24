package com.peco2282.bcreborn.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigGeneral {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("General settings").push("general");
    builder.pop();
    return builder;
  }
}
