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
import com.peco2282.bcreborn.transport.pipe.PipeGlassColor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Original {@code ItemPipe.addInformation}: a stained pipe names its colour. */
@Mod.EventBusSubscriber(modid = BCRebornTransport.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PipeTooltipHandler {
  private PipeTooltipHandler() {
  }

  @SubscribeEvent
  public static void onItemTooltip(ItemTooltipEvent event) {
    if (!PipeGlassColor.isPipe(event.getItemStack())) return;
    DyeColor color = PipeGlassColor.get(event.getItemStack());
    if (color == null) return;
    // Right after the name, like the original.
    event.getToolTip().add(Math.min(1, event.getToolTip().size()),
      Component.translatable("tip.bcreborntransport.pipe_stained",
        Component.translatable("color.minecraft." + color.getName())).withStyle(ChatFormatting.GRAY));
  }
}
