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
package com.peco2282.bcreborn.factory.event;

import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import com.peco2282.bcreborn.factory.FactoryMenuTypes;
import com.peco2282.bcreborn.factory.block.render.RenderHopper;
import com.peco2282.bcreborn.factory.block.render.RenderRefinery;
import com.peco2282.bcreborn.factory.block.render.RenderTank;
import com.peco2282.bcreborn.factory.screen.AutoCraftingScreen;
import com.peco2282.bcreborn.factory.screen.HopperScreen;
import com.peco2282.bcreborn.factory.screen.RefineryScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BCRebornFactory.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BCRebornFactoryEvent {

  public static final ModelLayerLocation HOPPER_LAYER = new ModelLayerLocation(BCRebornFactory.location("hopper"), "main");
  public static final ModelLayerLocation REFINERY_LAYER = new ModelLayerLocation(BCRebornFactory.location("refinery"), "main");

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      MenuScreens.register(FactoryMenuTypes.AUTO_WORKBENCH.get(), AutoCraftingScreen::new);
      MenuScreens.register(FactoryMenuTypes.HOPPER.get(), HopperScreen::new);
      MenuScreens.register(FactoryMenuTypes.REFINERY.get(), RefineryScreen::new);
    });
  }

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(FactoryBlockEntityTypes.HOPPER.get(), RenderHopper::new);
    event.registerBlockEntityRenderer(FactoryBlockEntityTypes.REFINERY.get(), RenderRefinery::new);
    event.registerBlockEntityRenderer(FactoryBlockEntityTypes.TANK.get(), RenderTank::new);
  }

  @SubscribeEvent
  public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(HOPPER_LAYER, RenderHopper::createLayer);
    event.registerLayerDefinition(REFINERY_LAYER, RenderRefinery::createLayer);
  }
}
