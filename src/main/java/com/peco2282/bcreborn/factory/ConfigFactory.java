package com.peco2282.bcreborn.factory;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigFactory {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Factory settings").push("factory");
    builder.pop();
    return builder;
  }
}
