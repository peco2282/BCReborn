package com.peco2282.bcreborn.transport.event;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.transport.TransportGameTests;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = BCRebornTransport.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GameTestRegistration {
  private static final Logger logger = BCReborn.createLogger();

  @SubscribeEvent
  public static void registerTests(RegisterGameTestsEvent event) {
    logger.info("Registering transport game tests");
    event.register(TransportGameTests.class);
    logger.info("Registered transport game tests");
  }
}
