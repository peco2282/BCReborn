package com.peco2282.bcreborn.builders.event;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornBuilders;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = BCRebornBuilders.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BCRebornBuildersEvent {
  private static final Logger logger = BCReborn.createLogger();

  public static void onServerStarting(ServerStartingEvent event) {
    logger.info("BCReborn Builders server starting event received");
  }
}
