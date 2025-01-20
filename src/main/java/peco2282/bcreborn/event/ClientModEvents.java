package peco2282.bcreborn.event;


import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.builder.block.BCBuilderBlocks;
import peco2282.bcreborn.builder.block.entity.BCBuilderBlockEntityTypes;
import peco2282.bcreborn.builder.block.entity.renderer.TankRenderer;
import peco2282.bcreborn.builder.block.menu.BCBuilderMenuTypes;
import peco2282.bcreborn.builder.block.screen.FillerScreen;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.block.screen.EngineIronScreen;
import peco2282.bcreborn.core.block.screen.EngineStoneScreen;
import peco2282.bcreborn.core.block.entity.BCCoreBlockEntityTypes;
import peco2282.bcreborn.core.block.entity.renderer.MarkerVolumeRenderer;
import peco2282.bcreborn.core.block.menu.BCCoreMenuTypes;
import peco2282.bcreborn.core.fluid.BCCoreFluids;
import peco2282.bcreborn.transport.block.BCTransportBlocks;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.entity.renderer.PipeRenderer;

/**
 * Handles client-side mod events for BCReborn, such as registering menu screens and entity renderers.
 * This class is automatically registered to the mod event bus to listen for and respond to events.
 *
 * @author peco2282
 */
@Mod.EventBusSubscriber(modid = BCReborn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
  private static final Logger log = LoggerFactory.getLogger(ClientModEvents.class);

  /**
   * Sets up client-specific configurations during the mod's client setup phase.
   * Registers menu screens and renders item blocks with specific render layers.
   *
   * @param event the FMLClientSetupEvent instance providing context for client setup operations
   */
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    log.trace("onClientSetup");
    log.trace("Register MenuScreens");
    MenuScreens.register(BCCoreMenuTypes.STONE_ENGINE.get(), EngineStoneScreen::new);
    MenuScreens.register(BCCoreMenuTypes.IRON_ENGINE.get(), EngineIronScreen::new);
    MenuScreens.register(BCBuilderMenuTypes.FILLER.get(), FillerScreen::new);
    log.trace("Done");
    log.trace("Register ItemBlockRenderTypes");
    ItemBlockRenderTypes.setRenderLayer(BCCoreBlocks.STONE_ENGINE.get(), RenderType.cutout());
    ItemBlockRenderTypes.setRenderLayer(BCCoreBlocks.IRON_ENGINE.get(), RenderType.cutout());
    ItemBlockRenderTypes.setRenderLayer(BCTransportBlocks.WOOD_ITEM_PIPE.get(), RenderType.cutout());

    ItemBlockRenderTypes.setRenderLayer(BCCoreFluids.OIL_SOURCE.get(), RenderType.translucent());
    ItemBlockRenderTypes.setRenderLayer(BCCoreFluids.OIL_FLOWING.get(), RenderType.translucent());
    ItemBlockRenderTypes.setRenderLayer(BCCoreFluids.FUEL_SOURCE.get(), RenderType.translucent());
    ItemBlockRenderTypes.setRenderLayer(BCCoreFluids.FUEL_FLOWING.get(), RenderType.translucent());
    log.trace("Done");
    // Some client setup code
//    LOGGER.info("HELLO FROM CLIENT SETUP");
//    LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
  }

  /**
   * Registers block entity renderers for use on the client.
   * Ensures proper handling of custom entities such as pipes, tanks, and markers.
   *
   * @param event the RegisterRenderers event used to register custom entity renderers
   */
  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(BCCoreBlockEntityTypes.MARKER_VOLUME.get(), MarkerVolumeRenderer::new);
    event.registerBlockEntityRenderer(BCTransportBlockEntities.WOODEN_ITEM_PIPE.get(), PipeRenderer::new);
    event.registerBlockEntityRenderer(BCBuilderBlockEntityTypes.TANK.get(), TankRenderer::new);
  }
}