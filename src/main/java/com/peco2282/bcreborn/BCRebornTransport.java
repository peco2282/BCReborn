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

import com.peco2282.bcreborn.api.facades.FacadeAPI;
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.api.transport.pluggable.PluggableType;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.ContextProcessor;
import com.peco2282.bcreborn.common.config.BCRebornConfigScreen;
import com.peco2282.bcreborn.robotics.station.RobotStationPluggable;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.pluggable.FacadePluggable;
import com.peco2282.bcreborn.transport.stripes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import static com.peco2282.bcreborn.api.transport.PipeManager.registerPipePluggable;

@Mod(BCRebornTransport.MODID)
public class BCRebornTransport implements BCReborn {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "bcreborntransport";
  // Directly reference a slf4j logger
  public static final Logger LOGGER = BCReborn.createLogger();
  private static final BCRegistry REGISTRY = BCRegistry.getRegistry(MODID);
  private static final ContextProcessor processor = ContextProcessor.create(MODID);

  public BCRebornTransport(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // Register ourselves for server apply other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
    processor.initRegister();

    REGISTRY.register(modEventBus);

    MinecraftForge.registerConfigScreen((mc, sc) -> new BCRebornConfigScreen(mc, sc, 7));
  }

  public static BCRegistry getRegistry() {
    return REGISTRY;
  }

  public static final PluggableType<FacadePluggable> FACADE = registerPipePluggable(location("facade"), FacadePluggable::new);
  public static final PluggableType<GatePluggable> GATE = registerPipePluggable(location("gate"), GatePluggable::new);
  public static final PluggableType<RobotStationPluggable> ROBOT_STATION = registerPipePluggable(location("robot_station"), RobotStationPluggable::new);

  public static ResourceLocation location(String path) {
    return BCReborn.getLocation(BCReborn.Type.TRANSPORT, path);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP");
    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

    FacadeAPI.facadeItem(TransportItems.FACADE.get());

    PipeManager.registerStripesHandler(StripesHandlerArrow.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerBucket.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerDispenser.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerEntityInteract.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerHoe.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerMinecartDestroy.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerPipes.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerPipeWires.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerPlaceBlock.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerPlant.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerRightClick.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerShears.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerUse.INSTANCE);
  }

  // You can use SubscribeEvent apply let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }
}
