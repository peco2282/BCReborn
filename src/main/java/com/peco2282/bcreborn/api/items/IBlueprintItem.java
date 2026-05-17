package com.peco2282.bcreborn.api.items;

import net.minecraft.world.item.ItemStack;

public interface IBlueprintItem extends INamedItem {
    enum Type {
        TEMPLATE, BLUEPRINT
    }

    Type getType(ItemStack stack);
}
