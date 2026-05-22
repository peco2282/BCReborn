/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.utils;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;

public final class ColorUtils {

	private static final int[] WOOL_TO_RGB = new int[]{
			0xFAFAFA, 0xD87F33, 0xB24CD8, 0x6699D8,
			0xE5E533, 0x7FCC19, 0xF27FA5, 0x4C4C4C,
			0x999999, 0x4C7F99, 0x7F3FB2, 0x334CB2,
			0x664C33, 0x667F33, 0x993333, 0x191919
	};

	private static final String[] WOOL_TO_NAME = new String[]{
			"white", "orange", "magenta", "light.blue",
			"yellow", "lime", "pink", "gray",
			"light.gray", "cyan", "purple", "blue",
			"brown", "green", "red", "black"
	};


	private static final char[] WOOL_TO_CHAT = new char[]{
			'f', '6', 'd', '9', 'e', 'a', 'd', '8',
			'7', '3', '5', '1', '6', '2', '4', '0'
	};

	private ColorUtils() {

	}

	public static void initialize() {
	}

	public static int getColorIDFromDye(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return -1;
		}

		if (stack.getItem() instanceof DyeItem dye) {
			return dye.getDyeColor().getId();
		}

		// Check for dye tags as fallback
		if (stack.is(Tags.Items.DYES_WHITE)) return DyeColor.WHITE.getId();
		if (stack.is(Tags.Items.DYES_ORANGE)) return DyeColor.ORANGE.getId();
		if (stack.is(Tags.Items.DYES_MAGENTA)) return DyeColor.MAGENTA.getId();
		if (stack.is(Tags.Items.DYES_LIGHT_BLUE)) return DyeColor.LIGHT_BLUE.getId();
		if (stack.is(Tags.Items.DYES_YELLOW)) return DyeColor.YELLOW.getId();
		if (stack.is(Tags.Items.DYES_LIME)) return DyeColor.LIME.getId();
		if (stack.is(Tags.Items.DYES_PINK)) return DyeColor.PINK.getId();
		if (stack.is(Tags.Items.DYES_GRAY)) return DyeColor.GRAY.getId();
		if (stack.is(Tags.Items.DYES_LIGHT_GRAY)) return DyeColor.LIGHT_GRAY.getId();
		if (stack.is(Tags.Items.DYES_CYAN)) return DyeColor.CYAN.getId();
		if (stack.is(Tags.Items.DYES_PURPLE)) return DyeColor.PURPLE.getId();
		if (stack.is(Tags.Items.DYES_BLUE)) return DyeColor.BLUE.getId();
		if (stack.is(Tags.Items.DYES_BROWN)) return DyeColor.BROWN.getId();
		if (stack.is(Tags.Items.DYES_GREEN)) return DyeColor.GREEN.getId();
		if (stack.is(Tags.Items.DYES_RED)) return DyeColor.RED.getId();
		if (stack.is(Tags.Items.DYES_BLACK)) return DyeColor.BLACK.getId();

		return -1;
	}

	public static boolean isDye(ItemStack stack) {
		return getColorIDFromDye(stack) >= 0;
	}

	public static int getRGBColor(int wool) {
		return WOOL_TO_RGB[wool & 15];
	}

	public static String getName(int wool) {
		return WOOL_TO_NAME[wool & 15];
	}

	public static String getOreDictionaryName(int wool) {
		return "dye" + getName(wool).replace(".", "");
	}

	public static String getFormatting(int wool) {
		return "\u00a7" + WOOL_TO_CHAT[wool & 15];
	}

	public static String getFormattingTooltip(int wool) {
		return "\u00a7" + (WOOL_TO_CHAT[wool & 15] == '0' ? '8' : WOOL_TO_CHAT[wool & 15]);
	}

}
