package com.peco2282.bcreborn.common.builder;

import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

public abstract class Schematic {
    public List<ItemStack> getStacksToDisplay(LinkedList<ItemStack> stackConsumed) {
        return stackConsumed != null ? stackConsumed : new LinkedList<>();
    }
}
