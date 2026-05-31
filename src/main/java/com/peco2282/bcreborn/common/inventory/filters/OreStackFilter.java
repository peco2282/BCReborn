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
package com.peco2282.bcreborn.common.inventory.filters;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Returns true if the stack matches any one one of the filter stacks.
 */
public class OreStackFilter implements IStackFilter {

	private final String[] ores;

	public OreStackFilter(String... iOres) {
		ores = iOres;
	}

	@Override
	public boolean matches(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		for (String ore : ores) {
			TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath("forge", ore));
			if (stack.is(tagKey)) {
				return true;
			}
		}

		return false;
	}
}
