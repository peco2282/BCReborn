/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.enums;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import peco2282.bcreborn.BCReborn;

public enum EnumEngineType implements StringRepresentable {
  WOOD("wood", 10),
  STONE("stone", 40),
  IRON("iron", 100),
  CREATIVE("creative", 100);

  public static final EnumEngineType[] VALUES = values();
  public static final Codec<EnumEngineType> CODEC =
      StringRepresentable.fromEnum(EnumEngineType::values);

  public final String unlocalizedTag;
  public final String resourceLocation;
  public final int output;

  EnumEngineType(String loc, int output) {
    unlocalizedTag = loc;
    resourceLocation = BCReborn.MODID + ":blocks/engine/inv/" + loc;
    this.output = output;
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
