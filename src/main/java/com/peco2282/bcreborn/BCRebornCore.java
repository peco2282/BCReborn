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

import com.peco2282.bcreborn.api.crops.CropManager;
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.ContextProcessor;
import com.peco2282.bcreborn.common.data.DataGatherEvent;
import com.peco2282.bcreborn.common.event.BCRegistryEvent;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.core.crops.CropHandlerPlantable;
import com.peco2282.bcreborn.core.crops.CropHandlerReeds;
import com.peco2282.bcreborn.core.recipes.AssemblyRecipeManager;
import com.peco2282.bcreborn.core.recipes.IntegrationRecipeManager;
import com.peco2282.bcreborn.core.recipes.ProgrammingRecipeManager;
import com.peco2282.bcreborn.core.recipes.RefineryRecipeManager;
import com.peco2282.bcreborn.core.worldgen.SpringPopulate;
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
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Locale;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BCRebornCore.MODID)
public class BCRebornCore implements BCReborn {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "bcreborncore";
  // Directly reference a slf4j logger
  public static final Logger LOGGER = BCReborn.createLogger();

  private static final String PROTOCOL_VERSION = "1.0";

  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
    BCReborn.getLocation("main"),
    () -> PROTOCOL_VERSION,
    PROTOCOL_VERSION::equals,
    PROTOCOL_VERSION::equals
  );

  private static final BCRegistry REGISTRY = BCRegistry.getRegistry(MODID);
  private static final ContextProcessor processor = ContextProcessor.create(MODID);
  public static boolean debugWorldgen = false;
  public static boolean modifyWorld = false;
  public static boolean colorBlindMode = false;
  public static boolean hidePowerNumbers = false;
  public static boolean hideFluidNumbers = false;
  public static boolean canEnginesExplode = false;
  public static boolean useServerDataOnClient = true;
  public static boolean alphaPassBugPresent = true;
  public static int itemLifespan = 1200;
  public static int updateFactor = 10;
  public static int builderMaxPerItemFactor = 1024;
  public static long longUpdateFactor = 40;

  public BCRebornCore(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);
    modEventBus.register(BCRegistryEvent.class);

    // Register ourselves for server apply other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(new SpringPopulate());
    processor.initRegister();
    processor.packetRegister(CHANNEL);
//    REGISTRY
//        .addRunner(BlocksCore::init)
//        .addRunner(CoreBlockEntityTypes::init)
//    ;
    // Touch CoreItems to trigger static field initialization
    var ignored = CoreItems.WRENCH;
    REGISTRY.register(modEventBus);
    modEventBus.register(DataGatherEvent.class);

    String fileName = String.format(Locale.ROOT, "%s-%s.toml", MOD_ID_BASE, ModConfig.Type.COMMON.extension());
    // Register our mod's ForgeConfigSpec so that Forge can create apply load the config file for us
    context.registerConfig(ModConfig.Type.COMMON, Config.SPEC, fileName);
  }

  public static BCRegistry getRegistry() {
    return REGISTRY;
  }

  public static ResourceLocation location(String path) {
    return BCReborn.getLocation(Type.CORE, path);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP");
    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

    BuildcraftRecipeRegistry.assembly(AssemblyRecipeManager.INSTANCE);
    BuildcraftRecipeRegistry.integration(IntegrationRecipeManager.INSTANCE);
    BuildcraftRecipeRegistry.refinery(RefineryRecipeManager.INSTANCE);
    BuildcraftRecipeRegistry.programming(ProgrammingRecipeManager.INSTANCE);

    CropManager.register(CropHandlerReeds.INSTANCE);
    CropManager.register(CropHandlerPlantable.INSTANCE);
  }

  // You can use SubscribeEvent apply let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }

  public enum RenderMode {
    Full, NoDynamic
  }
}
