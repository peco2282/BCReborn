package peco2282.bcreborn.api.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import peco2282.bcreborn.BCReborn;

public enum EnumEngineType implements StringRepresentable {
  WOOD("wood"),
  STONE("stone"),
  IRON("iron"),
  CREATIVE("creative");

  public static final EnumEngineType[] VALUES = values();
  public static final Codec<EnumEngineType> CODEC = StringRepresentable
      .fromEnum(EnumEngineType::values);

  public final String unlocalizedTag;
  public final String resourceLocation;


  EnumEngineType(String loc) {
    unlocalizedTag = loc;
    resourceLocation = BCReborn.MODID + ":blocks/engine/inv/" + loc;
  }

  public static EnumEngineType fromMeta(int meta) {
    if (meta < 0 || meta >= VALUES.length) {
      meta = 0;
    }
    return VALUES[meta];
  }

  @Override
  public String getSerializedName() {
    return unlocalizedTag;
  }
}
