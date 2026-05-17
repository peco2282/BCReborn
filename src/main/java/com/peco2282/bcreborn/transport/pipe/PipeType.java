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

  @Override
  public @NotNull String getSerializedName() {
    return name;
  }
}
