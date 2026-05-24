package com.peco2282.bcreborn.energy;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigEnergy {
  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Energy settings").push("energy");
    builder.pop();
    return builder;
  }
}
