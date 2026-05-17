package com.peco2282.bcreborn;

import com.mojang.logging.LogUtils;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.ContextProcessor;
import com.peco2282.bcreborn.core.ItemsCore;
import com.peco2282.bcreborn.common.data.DataGatherEvent;
import com.peco2282.bcreborn.common.data.ResourceDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
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
@Mod(BCRebornCore.MODID)
public class BCRebornCore implements BCReborn {
  // Define mod id in a common place for everything to reference
  public static final String MODID = "bcreborncore";
  // Directly reference a slf4j logger
  private static final Logger LOGGER = LogUtils.getLogger();

  private static final BCRegistry REGISTRY = BCRegistry.getRegistry(MODID);
  private static final ContextProcessor processor = ContextProcessor.create(MODID);

  public static BCRegistry getRegistry() {
    return REGISTRY;
  }

  public static ResourceLocation location(String path) {
    return BCReborn.getLocation(Type.CORE, path);
  }

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

  public enum RenderMode {
    Full, NoDynamic
  }


  public BCRebornCore(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // Register ourselves for server apply other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
    processor.initRegister();
//    REGISTRY
//        .addRunner(BlocksCore::init)
//        .addRunner(BlockEntityTypesCore::init)
//    ;
    // Touch ItemsCore to trigger static field initialization
    var ignored = ItemsCore.WRENCH;
    REGISTRY.register(modEventBus);
    modEventBus.register(DataGatherEvent.class);

    // Register our mod's ForgeConfigSpec so that Forge can create apply load the config file for us
    context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP");
    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
  }

  // You can use SubscribeEvent apply let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }
}
