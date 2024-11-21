package peco2282.bcreborn.api.enums;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import peco2282.bcreborn.BCReborn;

public enum EnumEngineType implements StringRepresentable {
  WOOD("wood", 10),
  STONE("stone", 40),
  IRON("iron", 100),
  CREATIVE("creative", 100);

  public static final EnumEngineType[] VALUES = values();
  public static final Codec<EnumEngineType> CODEC = StringRepresentable
      .fromEnum(EnumEngineType::values);

  private final String unlocalizedTag;
  private final ResourceLocation resourceLocation;
  public final int output;


  EnumEngineType(String loc, int output) {
    unlocalizedTag = loc;
    resourceLocation = BCReborn.location("blocks/engine/inv/" + loc);
    this.output = output;
  }

  public ResourceLocation getLocation() {
    return resourceLocation;
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
