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

import net.minecraft.locale.Language;

public final class StringUtils {

  public static final Splitter newLineSplitter = Splitter.on("\\n");

  /**
   * Deactivate constructor
   */
  private StringUtils() {
  }

  /**
   * The raw translation, not formatted: callers pass it to {@code String.format} themselves.
   * {@code I18n.get(key)} formats with no arguments, which turns a translation containing
   * {@code %s} into "Format error: ...".
   */
  public static String localize(String key) {
    return Language.getInstance().getOrDefault(key);
  }

  public static boolean canLocalize(String key) {
    return Language.getInstance().has(key);
  }
}