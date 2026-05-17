package com.peco2282.bcreborn.api.recipes;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IProgrammingRecipe {
    String getId();

    List<ItemStack> getOptions(int width, int height);

    int getEnergyCost(ItemStack option);

    boolean canCraft(ItemStack input);

    ItemStack craft(ItemStack input, ItemStack option);
}
