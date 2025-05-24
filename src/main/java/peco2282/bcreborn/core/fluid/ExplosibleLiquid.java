/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlock;

import java.util.function.Supplier;

public class ExplosibleLiquid extends LiquidBlock implements BCBlock {
  private final String id;

  /**
   * @param p_54694_ A fluid supplier such as {@link RegistryObject < FlowingFluid >}
   * @param p_54695_
   */
  public ExplosibleLiquid(
      Supplier<? extends FlowingFluid> p_54694_, Properties p_54695_, String id) {
    super(p_54694_, p_54695_);
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }

  // TODO explosion algo

  @Override
  protected void tick(
      BlockState p_222945_, ServerLevel p_222946_, BlockPos p_222947_, RandomSource p_222948_) {
    explode(p_222946_, p_222947_);
  }

  private static void explode(Level p_57437_, BlockPos p_57438_) {
    if (!p_57437_.isClientSide) {
      PrimedTnt primedtnt =
          new PrimedTnt(
              p_57437_, p_57438_.getX() + 0.5D, p_57438_.getY(), p_57438_.getZ() + 0.5D, null);
      p_57437_.addFreshEntity(primedtnt);
      p_57437_.playSound(
          null,
          primedtnt.getX(),
          primedtnt.getY(),
          primedtnt.getZ(),
          SoundEvents.TNT_PRIMED,
          SoundSource.BLOCKS,
          1.0F,
          1.0F);
      p_57437_.gameEvent(null, GameEvent.PRIME_FUSE, p_57438_);
    }
  }

  @Override
  protected void neighborChanged(
      BlockState p_54709_,
      Level p_54710_,
      BlockPos p_54711_,
      Block p_54712_,
      BlockPos p_54713_,
      boolean p_54714_) {
    super.neighborChanged(p_54709_, p_54710_, p_54711_, p_54712_, p_54713_, p_54714_);
    if (p_54710_.dimensionType().ultraWarm() || p_54712_.isBurning(p_54709_, p_54710_, p_54711_)) {
      p_54710_.scheduleTick(p_54711_, this, 20);
    }
  }
}
