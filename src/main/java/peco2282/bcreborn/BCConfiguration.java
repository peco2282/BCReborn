package peco2282.bcreborn;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = BCReborn.MODID)
public class BCConfiguration {
  static class Holder {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.Builder CORE = BUILDER.push("core");
    public static final ForgeConfigSpec.BooleanValue ENABLE_CREATIVE_ENGINE = BUILDER
        .comment(" Enable creative-engine")
        .define("enable_creative_engine", true);
  }
  static final ForgeConfigSpec SPEC = Holder.BUILDER.build();

  public static boolean enableCreativeEngine;

  @SubscribeEvent
  public static void onLoadConfig(ModConfigEvent event) {
    enableCreativeEngine = Holder.ENABLE_CREATIVE_ENGINE.get();
  }
}
