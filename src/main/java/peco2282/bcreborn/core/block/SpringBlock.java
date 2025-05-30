/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.enums.EnumSpring;
import peco2282.bcreborn.lib.block.BCBaseBlock;
import peco2282.bcreborn.utils.PropertyBuilder;

public class SpringBlock extends BCBaseBlock {
  public static final Property<EnumSpring> SPRING_TYPE = BCProperties.SPRING_TYPE;

  public SpringBlock(@NotNull String id) {
    super(
        Properties.of()
            .randomTicks()
            .explosionResistance(6000000.0F)
            .instabreak()
            .destroyTime(5.0F)
            .sound(SoundType.METAL),
        id,
        PropertyBuilder.builder().add(SPRING_TYPE, EnumSpring.WATER));
  }

  static boolean isAirBlock(Level level, BlockPos pos) {
    return level.getBlockState(pos).isAir();
  }

  @Override
  protected void tick(
      BlockState p_222945_, ServerLevel p_222946_, BlockPos p_222947_, RandomSource p_222948_) {
    generateSpringBlock(p_222946_, p_222947_, p_222945_, p_222948_);
  }

  private void generateSpringBlock(Level world, BlockPos pos, BlockState state, RandomSource rand) {
    EnumSpring spring = state.getValue(SPRING_TYPE);
    world.scheduleTick(pos, this, spring.getTickRate());
    if (!spring.canGen() || spring.getLiquidBlock() == null) {
      return;
    }
    if (!isAirBlock(world, pos.above())) {
      return;
    }
    if (spring.getChance() != -1 && rand.nextInt(spring.getChance()) != 0) {
      return;
    }
    world.setBlock(pos.above(), spring.getLiquidBlock(), 2);
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(SPRING_TYPE);
  }
}
