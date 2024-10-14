package peco2282.bcreborn;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BCReborn.MODID)
public class BCConfiguration {
  static class Holder {
    private static final ForgeConfigSpec.Builder SPEC_BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.Builder CORE = SPEC_BUILDER.push("core");
    public static final ForgeConfigSpec.BooleanValue ENABLE_CREATIVE_ENGINE = CORE
        .comment(" Enable creative-engine")
        .define("enable_creative_engine", true);
    public static final ForgeConfigSpec.IntValue MAX_VOLUME_BLOCK = CORE
        .comment(" Volume torch between size 'blockA' to 'blockB'")
        .defineInRange("max_volume_size", 32, 1, 32);
    public static final ForgeConfigSpec.Builder BUILDER = SPEC_BUILDER.push("builder");
    public static final ForgeConfigSpec.Builder TRANSPORT = SPEC_BUILDER.push("transport");
  }
  static final ForgeConfigSpec SPEC = Holder.SPEC_BUILDER.build();

  public static boolean enableCreativeEngine;
  public static int maxVolumeLength;

  @SubscribeEvent
  public static void onLoadConfig(ModConfigEvent event) {
    enableCreativeEngine = Holder.ENABLE_CREATIVE_ENGINE.get();
    maxVolumeLength = Holder.MAX_VOLUME_BLOCK.get();
  }
}
