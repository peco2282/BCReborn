package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumOptionalSnapshotType implements StringRepresentable {
  NONE(null),
  TEMPLATE(EnumSnapshotType.TEMPLATE),
  BLUEPRINT(EnumSnapshotType.BLUEPRINT);

  public final EnumSnapshotType type;

  EnumOptionalSnapshotType(EnumSnapshotType type) {
    this.type = type;
  }

  public static EnumOptionalSnapshotType fromNullable(EnumSnapshotType type) {
    if (type == null) {
      return NONE;
    }
    return switch (type) {
      case TEMPLATE -> TEMPLATE;
      case BLUEPRINT -> BLUEPRINT;
    };
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }
}
