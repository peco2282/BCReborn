/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.enums;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCProperties;

import java.util.Locale;
import java.util.function.Supplier;

public enum EnumSpring implements StringRepresentable {
  WATER(5, -1, Blocks.WATER.defaultBlockState()),
  OIL(6000, 8, null); // Set in BuildCraftEnergy

  public static final EnumSpring[] VALUES = values();

  private final int tickRate;
  private final int chance;
  private final String lowerCaseName = name().toLowerCase(Locale.ROOT);
  private final BlockState liquidBlock;
  private final boolean canGen = true;
  private Supplier<BlockEntity> tileConstructor;

  EnumSpring(int tickRate, int chance, BlockState liquidBlock) {
    this.tickRate = tickRate;
    this.chance = chance;
    this.liquidBlock = liquidBlock;
  }

  public static EnumSpring fromState(BlockState state) {
    return state.getValue(BCProperties.SPRING_TYPE);
  }

  public int getTickRate() {
    return tickRate;
  }

  public int getChance() {
    return chance;
  }

  public Supplier<BlockEntity> getTileConstructor() {
    return tileConstructor;
  }

  public boolean canGen() {
    return canGen;
  }

  public BlockState getLiquidBlock() {
    return liquidBlock;
  }

  @Override
  public String getSerializedName() {
    return lowerCaseName;
  }
}
