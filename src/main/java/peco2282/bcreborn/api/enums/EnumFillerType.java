/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import peco2282.bcreborn.builder.item.BCBuilderItems;
import peco2282.bcreborn.builder.item.FillerTypePanel;
import peco2282.bcreborn.utils.InventoryUtil;

import java.util.Optional;
import java.util.function.Supplier;

public enum EnumFillerType implements StringRepresentable {
  BOX(BCBuilderItems.BOX),
  CLEAR(BCBuilderItems.CLEAR),
  CYLINDER(BCBuilderItems.CYLINDER),
  FILL(BCBuilderItems.FILL),
  FLATTEN(BCBuilderItems.FLATTEN),
  HORIZONTAL(BCBuilderItems.HORIZONRAL),
  NONE(BCBuilderItems.NONE),
  PYRAMID(BCBuilderItems.PYRAMID),
  STAIR(BCBuilderItems.STAIR),
  ;

  private final Supplier<FillerTypePanel> panel;

  EnumFillerType(Supplier<FillerTypePanel> panel) {
    this.panel = panel;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }

  public static Optional<EnumFillerType> check(Container container) {
    if (container.getContainerSize() != 9) return Optional.empty();
    /*
    [s0, s1, s2]
    [s3, s4, s5]  [result]
    [s6, s7, s8]
     */
    ItemStack s0 = container.getItem(0);
    ItemStack s1 = container.getItem(1);
    ItemStack s2 = container.getItem(2);
    ItemStack s3 = container.getItem(3);
    ItemStack s4 = container.getItem(4);
    ItemStack s5 = container.getItem(5);
    ItemStack s6 = container.getItem(6);
    ItemStack s7 = container.getItem(7);
    ItemStack s8 = container.getItem(8);

    // BOX
    if (InventoryUtil.sameItemCheck(Items.BRICKS, s0, s1, s2, s3, s5, s6, s7, s8)
        && ItemStack.EMPTY.equals(s4)) {
      return Optional.of(EnumFillerType.BOX);
    }
    // CLEAR
    if (InventoryUtil.sameItemCheck(Items.GLASS, s0, s1, s2, s3, s5, s6, s7, s8)
        && ItemStack.EMPTY.equals(s4)) {
      return Optional.of(EnumFillerType.CLEAR);
    }
    return Optional.empty();
  }

  public FillerTypePanel getPanel() {
    return panel.get();
  }

  public void run() {}
}
