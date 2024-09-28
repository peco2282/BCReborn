package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCProperties;

public enum EnumMachineState implements StringRepresentable {
  OFF,
  ON,
  DONE;

  public static EnumMachineState getType(BlockState state) {
    return state.getValue(BCProperties.MACHINE_STATE);
  }
  @Override
  public String getSerializedName() {
    return name();
  }
}
