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

public enum PipeType implements StringRepresentable {
  ITEM("item"),
  FLUID("fluid"),
  ENERGY("energy"),
  /** Original {@code PipeTransportStructure}: carries nothing; it holds pluggables and pipe wires. */
  STRUCTURE("structure");

  private final String name;

  PipeType(String name) {
    this.name = name;
  }

  public boolean supports(PipeMaterial material) {
    return !material.unsupports(this);
  }

  @Override
  public String getSerializedName() {
    return name;
  }
}
