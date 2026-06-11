/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.core.recipes;

import com.peco2282.bcreborn.api.recipes.IFlexibleRecipeIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FlexibleRecipeIngredientOreStack implements IFlexibleRecipeIngredient {
    private final String oreName;
    private final int stackSize;

    public FlexibleRecipeIngredientOreStack(String oreName, int stackSize) {
        this.oreName = oreName;
        this.stackSize = stackSize;
    }

    @Override
    public Object getIngredient() {
        TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("forge", oreName));
        List<ItemStack> result = StreamSupport.stream(ForgeRegistries.ITEMS.getValues().spliterator(), false)
                .filter(item -> ForgeRegistries.ITEMS.tags().getTag(tagKey).contains(item))
                .map(item -> new ItemStack(item, stackSize))
                .collect(Collectors.toList());

        return result;
    }
}
