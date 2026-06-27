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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ConfigSection(
  MutableComponent title,
  List<ConfigEntry<?>> entries
) {
  public static Builder builder(MutableComponent title) {
    return new Builder(title);
  }

  public static class Builder {
    private final MutableComponent title;
    private final List<ConfigEntry<?>> entries;

    public Builder(MutableComponent title) {
      this.title = title;
      this.entries = new ArrayList<>();
    }

    public Builder addEntries(ConfigEntry<?>... entries) {
      Collections.addAll(this.entries, entries);
      return this;
    }

    public ConfigSection build() {
      return new ConfigSection(this.title, this.entries);
    }
  }
}
