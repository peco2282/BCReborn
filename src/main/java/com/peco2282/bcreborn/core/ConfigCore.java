package com.peco2282.bcreborn.core;

import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings("NotNullFieldNotInitialized")
public class ConfigCore {
  private static ForgeConfigSpec.IntValue itemLifespan;

  public static int getItemLifespan() {
    return itemLifespan.get();
  }

  public static ForgeConfigSpec.Builder load(ForgeConfigSpec.Builder builder) {
    builder.comment("Core settings").push("core");

    itemLifespan = builder.comment("Item lifespan in ticks (default: 1200)")
        .defineInRange("itemLifespan", 1200, 1, Integer.MAX_VALUE);

    builder.pop();

    return builder;
  }
}
