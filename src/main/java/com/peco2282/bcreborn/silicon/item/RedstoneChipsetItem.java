/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.silicon.item;

import com.peco2282.bcreborn.common.item.BuildCraftItem;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public class RedstoneChipsetItem extends BuildCraftItem {

	public enum Chipset {

		RED,
		IRON,
		GOLD,
		DIAMOND,
		PULSATING,
		QUARTZ,
		COMP,
		EMERALD;
		public static final Chipset[] VALUES = values();

		public String getChipsetName() {
			return "redstone_" + name().toLowerCase(Locale.ENGLISH) + "_chipset";
		}

		public static Chipset fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal >= VALUES.length) {
				return RED;
			}
			return VALUES[ordinal];
		}
	}

	private final Chipset chipset;

	public RedstoneChipsetItem(Chipset chipset) {
		super(new Properties());
		this.chipset = chipset;
	}

	public Chipset getChipset() {
		return chipset;
	}
}
