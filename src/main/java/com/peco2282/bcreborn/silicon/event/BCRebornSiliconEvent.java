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
package com.peco2282.bcreborn.silicon.event;

import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.SiliconMenuTypes;
import com.peco2282.bcreborn.silicon.block.render.LaserBlockEntityRenderer;
import com.peco2282.bcreborn.silicon.screen.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BCRebornSilicon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCRebornSiliconEvent {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      MenuScreens.register(SiliconMenuTypes.ASSEMBLY_TABLE.get(), AssemblyTableScreen::new);
      MenuScreens.register(SiliconMenuTypes.ADVANCED_CRAFTING_TABLE.get(), AdvancedCraftingTableScreen::new);
      MenuScreens.register(SiliconMenuTypes.INTEGRATION_TABLE.get(), IntegrationTableScreen::new);
      MenuScreens.register(SiliconMenuTypes.CHARGING_TABLE.get(), ChargingTableScreen::new);
      MenuScreens.register(SiliconMenuTypes.PROGRAMMING_TABLE.get(), ProgrammingTableScreen::new);
      MenuScreens.register(SiliconMenuTypes.STAMPING_TABLE.get(), StampingTableScreen::new);
      MenuScreens.register(SiliconMenuTypes.PACKAGER.get(), PackagerScreen::new);
    });
  }

  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(SiliconBlockEntityTypes.LASER.get(), LaserBlockEntityRenderer::new);
  }
}
