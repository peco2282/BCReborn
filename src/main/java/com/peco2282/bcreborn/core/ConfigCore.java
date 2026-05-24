package com.peco2282.bcreborn.core;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigCore {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Core settings").push("core");

    builder.pop();

    return builder;
  }
}
