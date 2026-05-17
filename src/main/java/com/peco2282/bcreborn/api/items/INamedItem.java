package com.peco2282.bcreborn.api.items;

import net.minecraft.world.item.ItemStack;

public interface INamedItem {
    String getName(ItemStack stack);

    boolean setName(ItemStack stack, String name);
}
