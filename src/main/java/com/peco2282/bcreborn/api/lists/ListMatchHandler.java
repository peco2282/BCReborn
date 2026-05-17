package com.peco2282.bcreborn.api.lists;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class ListMatchHandler {
    public enum Type {
        TYPE, MATERIAL, CLASS
    }

    public abstract boolean matches(Type type, ItemStack stack, ItemStack target, boolean precise);

    public abstract boolean isValidSource(Type type, ItemStack stack);

    public List<ItemStack> getClientExamples(Type type, ItemStack stack) {
        return null;
    }
}
