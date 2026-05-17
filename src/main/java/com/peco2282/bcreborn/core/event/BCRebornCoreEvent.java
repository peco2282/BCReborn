package com.peco2282.bcreborn.core.event;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.block.render.EngineBlockRenderer;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = BCRebornCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCRebornCoreEvent {
  private static final Logger logger = BCReborn.createLogger();

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    logger.info("Registering `Core` renderers");
    event.registerBlockEntityRenderer(BlockEntityTypesCore.WOODEN_ENGINE.get(), EngineBlockRenderer::new);
    logger.info("Registered `Core` renderers");
  }

  @SubscribeEvent
  public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
    logger.info("Registering `Core` layer definitions");
    event.registerLayerDefinition(EngineBlockRenderer.LAYER_LOCATION, EngineBlockRenderer::createLayer);
    logger.info("Registered `Core` layer definitions");
  }
}
