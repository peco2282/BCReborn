/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;
import peco2282.bcreborn.annotation.LateinitField;
import peco2282.bcreborn.bean.ContextProcessor;
import peco2282.bcreborn.data.DataGenerator;
import peco2282.bcreborn.event.CapabilityEvent;
import peco2282.bcreborn.event.internal.BCRebornEventBus;
import peco2282.bcreborn.event.internal.EventBus;
import peco2282.bcreborn.misc.Commands;
import peco2282.bcreborn.registry.BCRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BCReborn.MODID)
public class BCReborn {

  // Define mod id in a common place for everything to reference
  public static final String MODID = "bcreborn";
  public static final String VERSION = "1.0";
  public static final EventBus EVENT_BUS = BCRebornEventBus.getEventBus();
  //  // Create a Deferred Register to hold Blocks which will all be registered under the
  // "examplemod" namespace
  //  public static final DeferredRegister<Block> BLOCKS =
  // DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  //  // Creates a new Block with the id "examplemod:example_block", combining the namespace and
  // path
  //  public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", ()
  // -> new Block(BlockBehaviour.BCProperties.of().mapColor(MapColor.STONE)));
  //  // Create a Deferred Register to hold Items which will all be registered under the
  // "examplemod" namespace
  //  public static final DeferredRegister<Item> ITEMS =
  // DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
  //  // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and
  // path
  //  public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block",
  // () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.BCProperties()));
  //  // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
  //  public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () ->
  // new Item(new Item.BCProperties().food(new FoodProperties.Builder()
  //      .alwaysEdible().nutrition(1).saturationModifier(2f).build())));
  // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the
  // "examplemod" namespace
  //  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
  // DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
  //  // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is
  // placed after the combat tab
  //  public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB =
  // CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
  //      .withTabsBefore(CreativeModeTabs.COMBAT)
  //      .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
  //      .displayItems((parameters, output) -> {
  //        output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own
  // tabs, this method is preferred over the event
  //      }).build());
  // Directly reference a slf4j logger
  private static final Logger LOGGER = LogUtils.getLogger();
  @LateinitField public static FMLJavaModLoadingContext CONTEXT;

  public BCReborn(FMLJavaModLoadingContext context) {
    CONTEXT = context;
    IEventBus modEventBus = context.getModEventBus();

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    //    // Register the Deferred Register to the mod event bus so blocks get registered
    //    BLOCKS.register(modEventBus);
    //    // Register the Deferred Register to the mod event bus so items get registered
    //    ITEMS.register(modEventBus);
    // Register the Deferred Register to the mod event bus so tabs get registered
    BCRegistry.init(modEventBus);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(Commands.class);
    MinecraftForge.EVENT_BUS.register(CapabilityEvent.class);

    // Register the item to a creative tab
    modEventBus.addListener(this::addCreative);
    modEventBus.addListener(this::onNewRegistry);
    modEventBus.register(DataGenerator.class);

    // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
    context.registerConfig(ModConfig.Type.COMMON, BCConfiguration.SPEC);
    ContextProcessor.gatherBcEvent();
  }

  public static ResourceLocation location(String path) {
    return ResourceLocation.fromNamespaceAndPath(MODID, path);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP");

    //    if (Config.logDirtBlock)
    //      LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    //
    //    LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

    //    Config.items.forEach(item -> LOGGER.info("ITEM >> {}", item.toString()));
  }

  // Add the example block item to the building blocks tab
  private void addCreative(BuildCreativeModeTabContentsEvent event) {}

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }

  public void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {}
}
