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

import com.peco2282.bcreborn.core.recipes.AssemblyRecipeManager;
import com.peco2282.bcreborn.transport.recipes.FacadeAssemblyRecipes;
import com.peco2282.bcreborn.transport.statements.ActionParameterSignal;
import com.peco2282.bcreborn.transport.statements.TriggerParameterSignal;
import com.peco2282.bcreborn.core.statements.StatementParameterRedstoneGateSideOnly;
import com.peco2282.bcreborn.core.statements.StatementParameterDirection;
import com.peco2282.bcreborn.core.statements.StatementParameterItemStackExact;
import com.peco2282.bcreborn.api.statements.StatementParameterItemStack;
import com.peco2282.bcreborn.core.recipes.IntegrationRecipeManager;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.transport.gates.GateExpansionLightSensor;
import com.peco2282.bcreborn.transport.gates.GateExpansionRedstoneFader;
import com.peco2282.bcreborn.transport.gates.GateExpansionTimer;
import com.peco2282.bcreborn.transport.recipes.AdvancedFacadeRecipe;
import com.peco2282.bcreborn.transport.recipes.GateExpansionRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.peco2282.bcreborn.transport.statements.PipeTriggerProvider;
import com.peco2282.bcreborn.transport.statements.PipeActionProvider;
import com.peco2282.bcreborn.core.statements.DefaultActionProvider;
import com.peco2282.bcreborn.api.facades.FacadeAPI;
import com.peco2282.bcreborn.api.transport.PipeManager;
import com.peco2282.bcreborn.api.transport.pluggable.PluggableType;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.ContextProcessor;
import com.peco2282.bcreborn.common.config.ConfigScreenRegistration;
import com.peco2282.bcreborn.robotics.station.RobotStationPluggable;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.gates.GatePluggable;
import com.peco2282.bcreborn.transport.pipe.pluggable.FacadePluggable;
import com.peco2282.bcreborn.transport.pipe.pluggable.LensPluggable;
import com.peco2282.bcreborn.transport.pipe.pluggable.PlugPluggable;
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
import com.peco2282.bcreborn.api.gates.GateExpansions;
import com.peco2282.bcreborn.api.statements.StatementManager;
import com.peco2282.bcreborn.core.statements.DefaultTriggerProvider;
import com.peco2282.bcreborn.transport.gates.GateExpansionPulsar;

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
    MinecraftForge.EVENT_BUS.register(PipeExtensionListener.INSTANCE);
    processor.initRegister();

    REGISTRY.register(modEventBus);

    ConfigScreenRegistration.register(7);
  }

  public static BCRegistry getRegistry() {
    return REGISTRY;
  }

  public static final PluggableType<FacadePluggable> FACADE = registerPipePluggable(location("facade"), FacadePluggable::new);
  public static final PluggableType<GatePluggable> GATE = registerPipePluggable(location("gate"), GatePluggable::new);
  public static final PluggableType<PlugPluggable> PLUG = registerPipePluggable(location("plug"), PlugPluggable::new);
  public static final PluggableType<LensPluggable> LENS = registerPipePluggable(location("lens"), LensPluggable::new);
  public static final PluggableType<RobotStationPluggable> ROBOT_STATION = registerPipePluggable(location("robot_station"), RobotStationPluggable::new);

  public static ResourceLocation location(String path) {
    return BCReborn.getLocation(BCReborn.Type.TRANSPORT, path);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
      // Original TransportSiliconRecipes: each expansion is installed with its chipset on the Integration Table.
      GateExpansions.registerExpansion(GateExpansionPulsar.INSTANCE, new ItemStack(SiliconItems.PULSATING_CHIPSET.get()));
      GateExpansions.registerExpansion(GateExpansionTimer.INSTANCE, new ItemStack(SiliconItems.QUARTZ_CHIPSET.get()));
      GateExpansions.registerExpansion(GateExpansionRedstoneFader.INSTANCE, new ItemStack(SiliconItems.COMP_CHIPSET.get()));
      GateExpansions.registerExpansion(GateExpansionLightSensor.INSTANCE, new ItemStack(Items.DAYLIGHT_DETECTOR));
      IntegrationRecipeManager.INSTANCE.addRecipe(new GateExpansionRecipe());
      IntegrationRecipeManager.INSTANCE.addRecipe(new AdvancedFacadeRecipe());
      // Original ItemFacade.addFacade(): structure pipes + a block -> facades on the Assembly Table.
      AssemblyRecipeManager.addDynamicSource(FacadeAssemblyRecipes::create);
      // Parameter kinds a saved gate may name. Without these a gate with parameters could not be loaded.
      StatementManager.registerParameterFactory(StatementParameterItemStack::new);
      StatementManager.registerParameterFactory(StatementParameterItemStackExact::new);
      StatementManager.registerParameterFactory(StatementParameterDirection::new);
      StatementManager.registerParameterFactory(StatementParameterRedstoneGateSideOnly::new);
      StatementManager.registerParameterFactory(TriggerParameterSignal::new);
      StatementManager.registerParameterFactory(ActionParameterSignal::new);
      // Register the default triggers before any saved gate statement is read.
      StatementManager.registerTriggerProvider(
        new DefaultTriggerProvider());
      // Original BuildCraftCore / BuildCraftTransport registered these too; without them gates offered no
      // redstone output, machine control or pipe-specific statements.
      StatementManager.registerActionProvider(new DefaultActionProvider());
      StatementManager.registerTriggerProvider(PipeTriggerProvider.INSTANCE);
      StatementManager.registerActionProvider(PipeActionProvider.INSTANCE);
    });
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP");
    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

    FacadeAPI.facadeItem(TransportItems.FACADE.get());
    TransportItems.bindPipeWires();

    // Original BuildCraftTransport priorities: higher first, 0 is normal; placing blocks and the generic use come last.
    PipeManager.registerStripesHandler(StripesHandlerArrow.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerBucket.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerDispenser.INSTANCE, -49152);
    PipeManager.registerStripesHandler(StripesHandlerEntityInteract.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerHoe.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerMinecartDestroy.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerPipes.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerPipeWires.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerPlaceBlock.INSTANCE, -65536);
    PipeManager.registerStripesHandler(StripesHandlerPlant.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerRightClick.INSTANCE, -32768);
    PipeManager.registerStripesHandler(StripesHandlerShears.INSTANCE);
    PipeManager.registerStripesHandler(StripesHandlerUse.INSTANCE, -131072);
  }

  // You can use SubscribeEvent apply let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }
}
