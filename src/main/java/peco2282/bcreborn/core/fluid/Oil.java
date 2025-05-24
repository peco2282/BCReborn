/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidType;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.item.BCCoreItems;

public abstract sealed class Oil extends FlowingFluid permits Oil.Flowing, Oil.Source {
  @Override
  public Fluid getFlowing() {
    return BCCoreFluids.OIL_FLOWING.get();
  }

  @Override
  public Fluid getSource() {
    return BCCoreFluids.OIL_SOURCE.get();
  }

  /**
   * @param p_256009_
   * @deprecated Forge: Use {@link #canConvertToSource(FluidState, Level, BlockPos)} instead.
   */
  @Override
  @SuppressWarnings("dep-ann")
  protected boolean canConvertToSource(Level p_256009_) {
    return false;
  }

  @Override
  protected void beforeDestroyingBlock(
      LevelAccessor p_76002_, BlockPos p_76003_, BlockState p_76004_) {}

  @Override
  protected int getSlopeFindDistance(LevelReader p_76074_) {
    return 4;
  }

  @Override
  protected int getDropOff(LevelReader p_76087_) {
    return 1;
  }

  @Override
  public Item getBucket() {
    return BCCoreItems.OIL_BUCKET.get();
  }

  @Override
  protected boolean canBeReplacedWith(
      FluidState p_76127_,
      BlockGetter p_76128_,
      BlockPos p_76129_,
      Fluid p_76130_,
      Direction p_76131_) {
    return p_76127_.getHeight(p_76128_, p_76129_) >= 0.44444445F && p_76131_ == Direction.DOWN;
  }

  @Override
  public int getTickDelay(LevelReader p_76120_) {
    return 20;
  }

  @Override
  protected float getExplosionResistance() {
    return 300F;
  }

  @Override
  protected BlockState createLegacyBlock(FluidState p_76136_) {
    return BCCoreBlocks.OIL_SOURCE
        .get()
        .defaultBlockState()
        .setValue(LiquidBlock.LEVEL, getLegacyLevel(p_76136_));
  }

  @Override
  public boolean isSource(FluidState p_76140_) {
    return this instanceof Source;
  }

  @Override
  public FluidType getFluidType() {
    return BCCoreFluids.OIL.get();
  }

  @Override
  public boolean isSame(Fluid p_76122_) {
    return p_76122_ instanceof Oil;
  }

  public static final class Source extends Oil {
    @Override
    public int getAmount(FluidState p_164509_) {
      return 8;
    }
  }

  public static final class Flowing extends Oil {
    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76046_) {
      super.createFluidStateDefinition(p_76046_);
      p_76046_.add(LEVEL);
    }

    @Override
    public int getAmount(FluidState p_164509_) {
      return p_164509_.getValue(LEVEL);
    }
  }
}
