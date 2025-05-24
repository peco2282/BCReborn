/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.transport.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;

public enum PipeType implements StringRepresentable {
  ITEM("item"),
  @ApiStatus.Experimental
  FLUID("fluid"),
  @ApiStatus.Experimental
  ENERGY("energy");
  private final String type;

  PipeType(String type) {
    this.type = type;
  }

  @Override
  public String getSerializedName() {
    return type;
  }
}
