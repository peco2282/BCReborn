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
package com.peco2282.bcreborn.robotics.event;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.robotics.BlockEntityTypesRobotics;
import com.peco2282.bcreborn.robotics.RoboticsEntityTypes;
import com.peco2282.bcreborn.robotics.MenuTypesRobotics;
import com.peco2282.bcreborn.robotics.render.RenderRobot;
import com.peco2282.bcreborn.robotics.render.RenderZonePlan;
import com.peco2282.bcreborn.robotics.screen.RequesterScreen;
import com.peco2282.bcreborn.robotics.screen.ZonePlanScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BCRebornRobotics.MODID)
public class BCRebornRoboticsEvent {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      MenuScreens.register(MenuTypesRobotics.REQUESTER.get(), RequesterScreen::new);
      MenuScreens.register(MenuTypesRobotics.ZONE_PLAN.get(), ZonePlanScreen::new);
    });
  }

  @SubscribeEvent
  public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(RoboticsEntityTypes.ROBOT.get(), RenderRobot::new);
    event.registerBlockEntityRenderer(BlockEntityTypesRobotics.ZONE_PLAN.get(), RenderZonePlan::new);
  }
}
