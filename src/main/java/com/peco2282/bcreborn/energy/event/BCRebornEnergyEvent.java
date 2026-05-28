package com.peco2282.bcreborn.energy.event;

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.common.block.render.EngineBlockRenderer;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import com.peco2282.bcreborn.energy.BlockEntityTypesEnergy;
import com.peco2282.bcreborn.energy.MenuTypesEnergy;
import com.peco2282.bcreborn.energy.screen.IronEngineScreen;
import com.peco2282.bcreborn.energy.screen.StoneEngineScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = BCRebornEnergy.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCRebornEnergyEvent {

  private static final Logger logger = BCReborn.createLogger();

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      MenuScreens.register(MenuTypesEnergy.STONE_ENGINE.get(), StoneEngineScreen::new);
      MenuScreens.register(MenuTypesEnergy.IRON_ENGINE.get(), IronEngineScreen::new);
    });
  }


  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    logger.info("Registering `Energy` renderers");
    event.registerBlockEntityRenderer(BlockEntityTypesEnergy.CREATIVE_ENGINE.get(), EngineBlockRenderer::new);
    event.registerBlockEntityRenderer(BlockEntityTypesEnergy.STONE_ENGINE.get(), EngineBlockRenderer::new);
    event.registerBlockEntityRenderer(BlockEntityTypesEnergy.IRON_ENGINE.get(), EngineBlockRenderer::new);
    logger.info("Registered `Energy` renderers");
  }

//  @SubscribeEvent
//  public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
//    event.registerLayerDefinition(EngineBlockRenderer.LAYER_LOCATION, EngineBlockRenderer::createLayer);
//  }
}
