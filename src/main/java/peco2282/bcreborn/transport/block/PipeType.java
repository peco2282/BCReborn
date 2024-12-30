package peco2282.bcreborn.transport.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;

public enum PipeType implements StringRepresentable {
  ITEM("item"),
  @ApiStatus.Internal
  FLUID("fluid"),
  ENERGY("energy");
  private final String type;

  PipeType(String type) {
    this.type = type;
  }

  @Override
  public String getSerializedName() {
    return type;
  }
}