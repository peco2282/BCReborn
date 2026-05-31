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
package com.peco2282.bcreborn.common.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.HashMap;
import java.util.Map;

public class BCLanguageProvider extends LanguageProvider {
  private static final Map<String, String> EN_US = new HashMap<>();

  public BCLanguageProvider(PackOutput output, String modid, String locale) {
    super(output, modid, locale);
  }

  @Override
  protected void addTranslations() {
    add("itemGroup.bcreborn", "BCReborn");
  }
}
