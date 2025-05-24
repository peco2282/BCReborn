/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
