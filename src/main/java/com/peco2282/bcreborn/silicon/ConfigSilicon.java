package com.peco2282.bcreborn.silicon;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigSilicon {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Silicon settings").push("silicon");
    builder.pop();
    return builder;
  }
}
