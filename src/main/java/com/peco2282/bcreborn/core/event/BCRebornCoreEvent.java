package com.peco2282.bcreborn.core.event;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.block.render.EngineBlockRenderer;
import com.peco2282.bcreborn.core.client.render.MarkerBlockEntityRenderer;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import com.peco2282.bcreborn.core.MenuTypesCore;
import com.peco2282.bcreborn.core.item.ListItem;
import com.peco2282.bcreborn.core.list.ListNewScreen;
import com.peco2282.bcreborn.core.list.ListOldScreen;
import com.peco2282.bcreborn.core.item.MapLocationItem;
import com.peco2282.bcreborn.core.item.PaintbrushItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import com.peco2282.bcreborn.core.ItemsCore;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = BCRebornCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCRebornCoreEvent {
  private static final Logger logger = BCReborn.createLogger();

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    logger.info("Registering `Core` renderers");
    event.registerBlockEntityRenderer(BlockEntityTypesCore.WOODEN_ENGINE.get(), EngineBlockRenderer::new);
    event.registerBlockEntityRenderer(BlockEntityTypesCore.BLUE_MARKER.get(), MarkerBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(BlockEntityTypesCore.PATH_MARKER.get(), MarkerBlockEntityRenderer::new);
    logger.info("Registered `Core` renderers");
  }

  @SubscribeEvent
  public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
    logger.info("Registering `Core` layer definitions");
    event.registerLayerDefinition(EngineBlockRenderer.LAYER_LOCATION, EngineBlockRenderer::createLayer);
    logger.info("Registered `Core` layer definitions");
  }

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    logger.info("Performing client setup for `Core` layer");
    event.enqueueWork(() -> {
      MenuScreens.register(MenuTypesCore.LIST_OLD.get(), ListOldScreen::new);
      MenuScreens.register(MenuTypesCore.LIST_NEW.get(), ListNewScreen::new);

      ItemProperties.register(ItemsCore.LIST.get(), BCRebornCore.location(ListItem.TAG_WRITTEN),
              (stack, level, entity, seed) -> stack.getOrCreateTag().getBoolean(ListItem.TAG_WRITTEN) ? 1.0F : 0.0F);

      ItemProperties.register(ItemsCore.PAINTBRUSH.get(), BCRebornCore.location(PaintbrushItem.TAG_COLOR),
              (stack, level, entity, seed) -> PaintbrushItem.getColor(stack) + 1);

      ItemProperties.register(ItemsCore.MAP_LOCATION.get(), BCRebornCore.location(MapLocationItem.TAG_KIND),
              (stack, level, entity, seed) -> {
                  if (!stack.getOrCreateTag().contains(MapLocationItem.TAG_KIND)) {
                      return 0.0F;
                  }
                  return stack.getOrCreateTag().getByte(MapLocationItem.TAG_KIND) + 1.0F;
              });
    });
    logger.info("Client setup for `Core` layer complete");
  }
}
