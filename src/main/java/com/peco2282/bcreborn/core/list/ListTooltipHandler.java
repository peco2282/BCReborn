package com.peco2282.bcreborn.core.list;

import com.peco2282.bcreborn.api.items.IList;
import com.peco2282.bcreborn.common.utils.StringUtils;
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
