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
package com.peco2282.bcreborn.core.list;

import com.peco2282.bcreborn.api.items.IList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ListTooltipHandler {
  @SubscribeEvent
  public void itemTooltipEvent(ItemTooltipEvent event) {
    if (event.getItemStack().isEmpty() && event.getEntity() != null && event.getEntity().containerMenu != null
      && event.getEntity().containerMenu instanceof ListNewMenu) {
      ItemStack list = event.getEntity().getMainHandItem();
      if (list != null && list.getItem() instanceof IList) {
        if (((IList) list.getItem()).matches(list, event.getItemStack())) {
          event.getToolTip().add(Component.translatable("tip.list.matches").withStyle(ChatFormatting.GREEN));
        }
      }
    }
  }
}
