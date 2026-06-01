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
package com.peco2282.bcreborn.transport.pipe;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum PipeType implements StringRepresentable {
  ITEM("item"),
  FLUID("fluid"),
  ENERGY("energy");

  private final String name;

  PipeType(String name) {
    this.name = name;
  }

  public boolean supports(PipeMaterial material) {
    return !material.unsupports(this);
  }

  @Override
  public @NotNull String getSerializedName() {
    return name;
  }
}
