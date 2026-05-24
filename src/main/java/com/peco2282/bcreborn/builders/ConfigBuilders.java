package com.peco2282.bcreborn.builders;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilders {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Builders settings").push("builders");
    builder.pop();
    return builder;
  }
}
