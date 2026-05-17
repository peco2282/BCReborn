package com.peco2282.bcreborn.api.items;

import net.minecraft.world.item.ItemStack;

public interface IList extends INamedItem {
    @Deprecated
    String getLabel(ItemStack stack);

    boolean matches(ItemStack stackList, ItemStack item);
}
