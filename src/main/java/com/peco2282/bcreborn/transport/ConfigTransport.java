package com.peco2282.bcreborn.transport;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigTransport {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Transport settings").push("transport");
    builder.pop();
    return builder;
  }
}
