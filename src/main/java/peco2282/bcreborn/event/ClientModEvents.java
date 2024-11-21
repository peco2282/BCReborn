package peco2282.bcreborn.event;


import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.enums.EnumEngineType;
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
import peco2282.bcreborn.model.FunctionalBakedModel;
import peco2282.bcreborn.model.FunctionalGeometry;
import peco2282.bcreborn.transport.block.BCTransportBlocks;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.entity.renderer.PipeRenderer;

import java.util.List;
import java.util.Map;

// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@Mod.EventBusSubscriber(modid = BCReborn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
  private static final Logger log = LoggerFactory.getLogger(ClientModEvents.class);

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
    log.trace("Done");
    // Some client setup code
//    LOGGER.info("HELLO FROM CLIENT SETUP");
//    LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
  }

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(BCCoreBlockEntityTypes.MARKER_VOLUME.get(), MarkerVolumeRenderer::new);
    event.registerBlockEntityRenderer(BCTransportBlockEntities.WOODEN_ITEM_PIPE.get(), PipeRenderer::new);
    event.registerBlockEntityRenderer(BCBuilderBlockEntityTypes.TANK.get(), TankRenderer::new);
  }

  @SubscribeEvent
  public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
    event.register("functional", FunctionalGeometry.LOADER);
    log.info("Registered geometry");
  }

  @SubscribeEvent
  public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
    Map<ModelResourceLocation, BakedModel> models = event.getModels();
    models.put(ModelResourceLocation.inventory(EnumEngineType.WOOD.getLocation()), new FunctionalBakedModel(List.of(), true));
  }
}