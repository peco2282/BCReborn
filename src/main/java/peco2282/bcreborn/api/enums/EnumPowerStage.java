package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum EnumPowerStage implements StringRepresentable {
  BLUE(3, 0),
  GREEN(5, 50),
  YELLOW(8, 100),
  RED(10, 200),
  OVERHEAT(0, 0),
  BLACK(0, 0);

  private final int power;
  private final int threshold;
  EnumPowerStage(final int power, final int threshold) {
    this.power = power;
    this.threshold = threshold;
  }

  public static final EnumPowerStage[] VALUES = values();

  private final String modelName = name().toLowerCase(Locale.ROOT);

  public String getModelName() {
    return modelName;
  }

  public int power() {
    return power;
  }

  public int threshold() {
    return threshold;
  }

  public boolean hasPrev() {
    return isRunning();
  }

  public EnumPowerStage prev() {
    return switch (this) {
      case BLUE -> BLACK;
      case GREEN -> BLUE;
      case YELLOW -> GREEN;
      case RED -> YELLOW;
      case OVERHEAT -> BLACK.prev();
      case BLACK -> this;
    };
  }

  public boolean hasNext() {
    return this != OVERHEAT;
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
