package com.peco2282.bcreborn.builders.item;

import net.minecraft.world.item.ItemStack;

public class BlueprintTemplateItem extends BlueprintItem {

    protected BlueprintTemplateItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getIconType() {
        return "template";
    }

    @Override
    public Type getType(ItemStack stack) {
        return Type.TEMPLATE;
    }
}
