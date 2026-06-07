/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn;

import com.peco2282.bcreborn.builders.BuildersConfig;
import com.peco2282.bcreborn.common.GeneralConfig;
import com.peco2282.bcreborn.core.ConfigCore;
import com.peco2282.bcreborn.energy.EnergyConfig;
import com.peco2282.bcreborn.factory.FactoryConfig;
import com.peco2282.bcreborn.robotics.RoboticsConfig;
import com.peco2282.bcreborn.silicon.SiliconConfig;
import com.peco2282.bcreborn.transport.TransportConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = BCRebornCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
  public static final ForgeConfigSpec SPEC;
  private static final Logger LOGGER = BCReborn.createLogger();
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  static {
    GeneralConfig.load(BUILDER);

    ConfigCore.load(BUILDER); // Core settings
    EnergyConfig.load(BUILDER); // Energy settings
    TransportConfig.load(BUILDER); // Transport settings
    BuildersConfig.load(BUILDER); // Builders settings
    FactoryConfig.load(BUILDER); // Factory settings
    SiliconConfig.load(BUILDER); // Silicon settings
    RoboticsConfig.load(BUILDER); // Robotics settings

    SPEC = BUILDER.build();
  }

  @SubscribeEvent
  static void onLoad(final ModConfigEvent event) {
    if (event.getConfig().getModId().equals(BCRebornCore.MODID)) {
      LOGGER.info("Config loaded: {}", event.getConfig().getFileName());
    }
  }
}
