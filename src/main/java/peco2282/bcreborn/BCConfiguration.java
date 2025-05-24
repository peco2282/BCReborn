/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * BCConfiguration handles the configuration settings for the mod. It initializes and maintains the
 * configuration values and provides mechanisms to load them during runtime.
 *
 * @author peco2282
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BCReborn.MODID)
public class BCConfiguration {
  static class Holder {
    private static final ForgeConfigSpec.Builder SPEC_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.Builder CORE = SPEC_BUILDER.push("core");
    public static final ForgeConfigSpec.BooleanValue ENABLE_CREATIVE_ENGINE =
        CORE.comment(" Enable creative-engine").define("enable_creative_engine", true);
    public static final ForgeConfigSpec.IntValue MAX_VOLUME_BLOCK =
        CORE.comment(" Volume torch between size 'blockA' to 'blockB'")
            .defineInRange("max_volume_size", 32, 1, 32);
    public static final ForgeConfigSpec.Builder BUILDER = SPEC_BUILDER.push("builder");
    public static final ForgeConfigSpec.Builder TRANSPORT = SPEC_BUILDER.push("transport");
  }

  static final ForgeConfigSpec SPEC = Holder.SPEC_BUILDER.build();

  /** Indicates whether the creative engine is enabled in the mod configuration. */
  public static boolean enableCreativeEngine;

  /** Represents the maximum volume size setting for blocks in the configuration. */
  public static int maxVolumeLength;

  /**
   * Loads the configuration settings when the specified configuration event occurs. Updates the
   * mod's runtime configuration values based on the defined settings.
   *
   * @param event the mod configuration event
   */
  @SubscribeEvent
  public static void onLoadConfig(ModConfigEvent event) {
    enableCreativeEngine = Holder.ENABLE_CREATIVE_ENGINE.get();
    maxVolumeLength = Holder.MAX_VOLUME_BLOCK.get();
  }
}
