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
package com.peco2282.bcreborn.common.utils;

import com.google.common.base.Splitter;

import net.minecraft.client.resources.language.I18n;

public final class StringUtils {

	public static final Splitter newLineSplitter = Splitter.on("\\n");

	/**
	 * Deactivate constructor
	 */
	private StringUtils() {
	}

	public static String localize(String key) {
		return I18n.get(key);
	}

	public static boolean canLocalize(String key) {
		return I18n.exists(key);
	}
}