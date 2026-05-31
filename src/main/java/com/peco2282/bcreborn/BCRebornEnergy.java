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

import com.mojang.logging.LogUtils;
import com.peco2282.bcreborn.api.fuels.BuildcraftFuelRegistry;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.ContextProcessor;
import com.peco2282.bcreborn.energy.FluidsEnergy;
import com.peco2282.bcreborn.energy.worldgen.OilPopulate;
import com.peco2282.bcreborn.energy.fuel.CoolantManager;
import com.peco2282.bcreborn.energy.fuel.FuelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BCRebornEnergy.MODID)
public class BCRebornEnergy implements BCReborn {

  // Define mod id in a common place for everything to reference
  public static final String MODID = "bcrebornenergy";
  // Directly reference a slf4j logger
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final BCRegistry REGISTRY = BCRegistry.getRegistry(MODID);
  private static final ContextProcessor processor = ContextProcessor.create(MODID);

  public static BCRegistry getRegistry() {
    return REGISTRY;
  }

  public static ResourceLocation location(String path) {
    return BCReborn.getLocation(Type.ENERGY, path);
  }

  public BCRebornEnergy(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // Register ourselves for server apply other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
//    MinecraftForge.EVENT_BUS.register(OilPopulate.INSTANCE);
    processor.initRegister();

    var ignoredFluids = FluidsEnergy.OIL_SOURCE;
    FluidsEnergy.registerFluidTypes(modEventBus);
    REGISTRY.register(modEventBus);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP");
    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

    initFuel();

    BuildcraftFuelRegistry.setCoolantManager(
        CoolantManager.INSTANCE
    );
  }

  private static void initFuel() {
    BuildcraftFuelRegistry.setFuelManager(
        FuelManager.INSTANCE
    );

    BuildcraftFuelRegistry.getFuelManager().addFuel(FluidsEnergy.OIL_SOURCE.get(), 1 ,1);
  }

  // You can use SubscribeEvent apply let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }
}
