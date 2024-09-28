package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumPowerStage implements StringRepresentable {
  BLUE(.3F, 0),
  GREEN(.5F, 50),
  YELLOW(.8F, 100),
  RED(1F, 200),
  OVERHEAT(0F, 0),
  BLACK(0F, 0);

  private final float power;
  private final int threshold;
  EnumPowerStage(final float power, final int threshold) {
    this.power = power;
    this.threshold = threshold;
  }

  public static final EnumPowerStage[] VALUES = values();

  private final String modelName = name().toLowerCase(Locale.ROOT);

  public String getModelName() {
    return modelName;
  }

  public float power() {
    return power;
  }

  public int threshold() {
    return threshold;
  }

  public EnumPowerStage next() {
    return switch (this) {
      case BLUE -> GREEN;
      case GREEN -> YELLOW;
      case YELLOW, RED -> RED;
      case OVERHEAT -> BLACK.next();
      case BLACK -> this;
    };
  }

  public boolean isRunning() {
    return this != BLACK && this != OVERHEAT;
  }
  @Override
  public String getSerializedName() {
    return getModelName();
  }

}
