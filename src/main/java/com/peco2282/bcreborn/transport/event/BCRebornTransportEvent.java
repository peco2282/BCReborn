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
package com.peco2282.bcreborn.transport.event;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.transport.TransportBlockEntityTypes;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.TransportMenuTypes;
import com.peco2282.bcreborn.transport.block.render.EnergyPipeRenderer;
import com.peco2282.bcreborn.transport.block.render.FluidPipeRenderer;
import com.peco2282.bcreborn.transport.block.render.ItemPipeRenderer;
import com.peco2282.bcreborn.transport.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BCRebornTransport.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BCRebornTransportEvent {

  @SubscribeEvent
  @SuppressWarnings("removal")
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      TransportBlocks.pipesForEach((type, material, block) -> {
        if (block != null) {
          ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
        }
      });

      MenuScreens.register(TransportMenuTypes.DIAMOND_PIPE_MENU.get(), DiamondPipeScreen::new);
      MenuScreens.register(TransportMenuTypes.EMERALD_PIPE_MENU.get(), EmeraldPipeScreen::new);
      MenuScreens.register(TransportMenuTypes.EMERALD_FLUID_PIPE_MENU.get(), EmeraldFluidPipeScreen::new);
      MenuScreens.register(TransportMenuTypes.EMZULI_PIPE_MENU.get(), EmzuliPipeScreen::new);
      MenuScreens.register(TransportMenuTypes.FILTERED_BUFFER_MENU.get(), FilteredBufferScreen::new);
      MenuScreens.register(TransportMenuTypes.GATE_INTERFACE_MENU.get(), GateInterfaceScreen::new);
    });
  }

  @SubscribeEvent
  public static void onRegisterBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(TransportBlockEntityTypes.ITEM_PIPE.get(), ItemPipeRenderer::new);
    event.registerBlockEntityRenderer(TransportBlockEntityTypes.FLUID_PIPE.get(), FluidPipeRenderer::new);
    event.registerBlockEntityRenderer(TransportBlockEntityTypes.ENERGY_PIPE.get(), EnergyPipeRenderer::new);
  }
}
