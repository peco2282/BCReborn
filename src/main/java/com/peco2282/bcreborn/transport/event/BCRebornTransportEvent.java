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

import com.peco2282.bcreborn.api.statements.StatementManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;
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
import net.minecraftforge.client.event.ModelEvent;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import com.peco2282.bcreborn.transport.block.render.ColoredPipeModel;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraft.world.item.DyeColor;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.item.LensItem;
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
      // Original ItemGateCopier icons: empty, or full once a gate was copied.
      net.minecraft.client.renderer.item.ItemProperties.register(TransportItems.GATE_COPIER.get(), BCRebornTransport.location("full"),
        (stack, level, entity, seed) -> com.peco2282.bcreborn.transport.item.GateCopierItem.hasCopy(stack) ? 1.0F : 0.0F);
    });
  }

  @SubscribeEvent
  public static void onRegisterBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(TransportBlockEntityTypes.ITEM_PIPE.get(), ItemPipeRenderer::new);
    event.registerBlockEntityRenderer(TransportBlockEntityTypes.FLUID_PIPE.get(), FluidPipeRenderer::new);
    event.registerBlockEntityRenderer(TransportBlockEntityTypes.ENERGY_PIPE.get(), EnergyPipeRenderer::new);
    // A structure pipe has nothing inside; the item renderer draws its pluggables and wires.
    event.registerBlockEntityRenderer(TransportBlockEntityTypes.STRUCTURE_PIPE.get(), ItemPipeRenderer::new);
  }

  /** Original ItemLens.getColorFromItemStack(): layer 0 (the glass) takes the lens colour, the frame stays as is. */
  @SubscribeEvent
  public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
    for (boolean filter : new boolean[]{false, true}) {
      for (DyeColor color : DyeColor.values()) {
        LensItem item = TransportItems.lens(color, filter);
        event.register((stack, tintIndex) -> tintIndex == 0 ? item.getTint() : 0xFFFFFF, item);
      }
    }
  }

  /**
   * Lapis and Daizuli pipes take their texture from the pipe's colour: their block models are wrapped so the
   * colour (model data) picks the texture.
   */
  @SubscribeEvent
  public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
    wrapColoredPipe(event, PipeMaterial.LAPIS, "block/pipes/pipe_items_lapis_", DyeColor.BLUE);
    wrapColoredPipe(event, PipeMaterial.DAIZULI, "block/pipes/pipe_items_daizuli_", DyeColor.BLACK);
  }

  private static void wrapColoredPipe(ModelEvent.ModifyBakingResult event, PipeMaterial material, String texturePrefix, DyeColor builtIn) {
    var block = TransportBlocks.get(PipeType.ITEM, material);
    if (block == null) return;
    // Every state of a multipart block shares one baked model; keep sharing one wrapper.
    Map<BakedModel, BakedModel> wrapped = new IdentityHashMap<>();
    for (BlockState state : block.get().getStateDefinition().getPossibleStates()) {
      event.getModels().computeIfPresent(BlockModelShaper.stateToModelLocation(state),
        (location, model) -> wrapped.computeIfAbsent(model, original -> new ColoredPipeModel(original, texturePrefix, builtIn)));
    }
  }

  /**
   * The gate statement icons (original registerIcons on the item atlas). The textures in every module's
   * {@code textures/item/triggers} are stitched into the block atlas as {@code <mod>:triggers/<name>}
   * ({@code assets/minecraft/atlases/blocks.json}); before, they were never loaded and the gate GUI had no icons.
   */
  @SubscribeEvent
  public static void onTextureStitchPost(TextureStitchEvent.Post event) {
    if (!event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) return;
    StatementManager.registerIcons(event.getAtlas()::getSprite);
  }
}
