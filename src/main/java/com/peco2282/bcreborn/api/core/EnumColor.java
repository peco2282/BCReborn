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
package com.peco2282.bcreborn.api.core;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumColor implements StringRepresentable {
  WHITE,
  ORANGE,
  MAGENTA,
  LIGHT_BLUE,
  YELLOW,
  LIME,
  PINK,
  GRAY,
  LIGHT_GRAY,
  CYAN,
  PURPLE,
  BLUE,
  BROWN,
  GREEN,
  RED,
  BLACK;

  public static final EnumColor[] VALUES = values();

  public static EnumColor fromId(int id) {
    if (id < 0 || id >= VALUES.length) {
      return WHITE;
    }
    return VALUES[id];
  }

  public String getTag() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  public String getName() {
    String name = name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
    return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
  }

  public EnumColor getNext() {
    return VALUES[(ordinal() + 1) % VALUES.length];
  }

  public EnumColor getPrevious() {
    return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ENGLISH);
  }
}
