package com.peco2282.bcreborn.transport.event;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.transport.BlockEntityTypesTransport;
import com.peco2282.bcreborn.transport.BlocksTransport;
import com.peco2282.bcreborn.transport.block.render.EnergyPipeRenderer;
import com.peco2282.bcreborn.transport.block.render.FluidPipeRenderer;
import com.peco2282.bcreborn.transport.block.render.ItemPipeRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
      BlocksTransport.PIPES_BY_MAT.forEach((material, typeMap) -> typeMap.forEach((type, block) -> {
        //noinspection removal
        ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout());
      }));
    });
  }

  @SubscribeEvent
  public static void onRegisterBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(BlockEntityTypesTransport.ITEM_PIPE.get(), ItemPipeRenderer::new);
    event.registerBlockEntityRenderer(BlockEntityTypesTransport.FLUID_PIPE.get(), FluidPipeRenderer::new);
    event.registerBlockEntityRenderer(BlockEntityTypesTransport.ENERGY_PIPE.get(), EnergyPipeRenderer::new);
  }
}
