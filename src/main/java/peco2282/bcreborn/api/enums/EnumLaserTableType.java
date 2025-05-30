/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;

public enum EnumLaserTableType implements StringRepresentable {
  ASSEMBLY_TABLE,
  ADVANCED_CRAFTING_TABLE,
  INTEGRATION_TABLE,
  CHARGING_TABLE,
  PROGRAMMING_TABLE;

  @Override
  public String getSerializedName() {
    return name();
  }
}
