package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumDecoratedBlock implements StringRepresentable {
  DESTROY(0),
  BLUEPRINT(10),
  TEMPLATE(10),
  PAPER(10),
  LEATHER(10),
  LASER_BACK(0);

  private static final EnumDecoratedBlock[] VALUES = values();

  private final int lightValue;

  EnumDecoratedBlock(int lightValue) {
    this.lightValue = lightValue;
  }

  public static EnumDecoratedBlock fromMeta(int meta) {
    if (meta < 0 || meta >= VALUES.length) {
      return EnumDecoratedBlock.DESTROY;
    }
    return VALUES[meta];
  }

  public int getLightValue() {
    return lightValue;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
