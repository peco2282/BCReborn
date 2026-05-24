package com.peco2282.bcreborn;

import com.peco2282.bcreborn.builders.ConfigBuilders;
import com.peco2282.bcreborn.common.ConfigGeneral;
import com.peco2282.bcreborn.core.ConfigCore;
import com.peco2282.bcreborn.energy.ConfigEnergy;
import com.peco2282.bcreborn.factory.ConfigFactory;
import com.peco2282.bcreborn.robotics.ConfigRobotics;
import com.peco2282.bcreborn.silicon.ConfigSilicon;
import com.peco2282.bcreborn.transport.ConfigTransport;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = BCRebornCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  private static final Logger LOGGER = BCReborn.createLogger();

  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  private static final ForgeConfigSpec.Builder GENERAL;
  private static final ForgeConfigSpec.Builder CORE;
  private static final ForgeConfigSpec.Builder ENERGY;
  private static final ForgeConfigSpec.Builder TRANSPORT;
  private static final ForgeConfigSpec.Builder BUILDERS;
  private static final ForgeConfigSpec.Builder FACTORY;
  private static final ForgeConfigSpec.Builder SILICON;
  private static final ForgeConfigSpec.Builder ROBOTICS;

  public static final ForgeConfigSpec SPEC;

  static {
    GENERAL = ConfigGeneral.load(BUILDER);

    CORE = ConfigCore.load(BUILDER); // Core settings
    ENERGY = ConfigEnergy.load(BUILDER); // Energy settings
    TRANSPORT = ConfigTransport.load(BUILDER); // Transport settings
    BUILDERS = ConfigBuilders.load(BUILDER); // Builders settings
    FACTORY = ConfigFactory.load(BUILDER); // Factory settings
    SILICON = ConfigSilicon.load(BUILDER); // Silicon settings
    ROBOTICS = ConfigRobotics.load(BUILDER); // Robotics settings

    SPEC = BUILDER.build();
  }

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    if (event.getConfig().getModId().equals(BCRebornCore.MODID)) {
      LOGGER.info("Config loaded: {}", event.getConfig().getFileName());
    }
  }
}
