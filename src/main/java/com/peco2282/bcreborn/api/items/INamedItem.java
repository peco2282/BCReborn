package com.peco2282.bcreborn.api.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface INamedItem {
    Component getName(ItemStack stack);

    boolean setName(ItemStack stack, Component name);
}
