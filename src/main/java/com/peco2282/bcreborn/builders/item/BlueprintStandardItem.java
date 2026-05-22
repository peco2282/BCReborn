package com.peco2282.bcreborn.builders.item;

import net.minecraft.world.item.ItemStack;

public class BlueprintStandardItem extends BlueprintItem {
    public BlueprintStandardItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getIconType() {
        return "blueprint";
    }

    @Override
    public Type getType(ItemStack stack) {
        return Type.BLUEPRINT;
    }
}
