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
package com.peco2282.bcreborn.common.config;

import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record ConfigEntry<T>(
  String name,
  MutableComponent title,
  MutableComponent tooltip,
  EntryType type,
  T defaultValue,
  Supplier<T> getter,
  Consumer<T> setter,
  Predicate<T> validator
) {
  public static ConfigEntry<Integer> integerOf(
    String name,
    MutableComponent title,
    MutableComponent tooltip,
    ForgeConfigSpec.IntValue value,
    Predicate<Integer> validator
  ) {
    return new ConfigEntry<>(name, title, tooltip, EntryType.INTEGER, value.getDefault(), value, value::set, validator);
  }

  public static ConfigEntry<Double> doubleOf(
    String name,
    MutableComponent title,
    MutableComponent tooltip,
    ForgeConfigSpec.DoubleValue value,
    Predicate<Double> validator
  ) {
    return new ConfigEntry<>(name, title, tooltip, EntryType.DOUBLE, value.getDefault(), value, value::set, validator);
  }

  public static <T extends Enum<T>> ConfigEntry<T> enumOf(
    String name,
    MutableComponent title,
    MutableComponent tooltip,
    ForgeConfigSpec.EnumValue<T> value,
    Predicate<T> validator
  ) {
    return new ConfigEntry<>(name, title, tooltip, EntryType.ENUM, value.getDefault(), value, value::set, validator);
  }

  public static ConfigEntry<Boolean> booleanOf(
    String name,
    MutableComponent title,
    MutableComponent tooltip,
    ForgeConfigSpec.BooleanValue value
  ) {
    return new ConfigEntry<>(name, title, tooltip, EntryType.BOOLEAN, value.getDefault(), value, value::set, t -> true);
  }

  public static ConfigEntry<String> stringOf(
    String name,
    MutableComponent title,
    MutableComponent tooltip,
    ForgeConfigSpec.ConfigValue<String> value,
    Predicate<String> validator
  ) {
    return new ConfigEntry<>(name, title, tooltip, EntryType.STRING, value.getDefault(), value, value::set, validator);
  }

  public static ConfigEntry<List<String>> stringListOf(
    String name,
    MutableComponent title,
    MutableComponent tooltip,
    ForgeConfigSpec.ConfigValue<List<String>> value,
    Predicate<List<String>> validator
  ) {
    return new ConfigEntry<>(name, title, tooltip, EntryType.STRING_LIST, value.getDefault(), value, value::set, validator);
  }

  public static <T extends Comparable<T>> Predicate<T> range(T min, T max) {
    return t -> t.compareTo(min) >= 0 && t.compareTo(max) <= 0;
  }

  public enum EntryType {
    INTEGER,
    DOUBLE,
    ENUM,
    BOOLEAN,
    STRING,
    STRING_LIST
  }
}
