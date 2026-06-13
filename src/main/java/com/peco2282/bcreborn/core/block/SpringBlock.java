/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.core.block;

import com.peco2282.bcreborn.common.utils.XorShift128Random;
import com.peco2282.bcreborn.energy.EnergyConfig;
import com.peco2282.bcreborn.energy.EnergyFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

public class SpringBlock extends Block {
  public static final EnumProperty<EnumSpring> TYPE = EnumProperty.create("type", EnumSpring.class);
  private static final XorShift128Random random = new XorShift128Random();

  public SpringBlock() {
    super(Properties.of()
      .mapColor(MapColor.STONE)
      .strength(-1.0F, 6000000.0F)
      .sound(SoundType.STONE)
      .randomTicks());
    this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, EnumSpring.WATER));
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(TYPE);
  }

  @Override
  public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    assertSpring(level, pos, state);
  }

  @Override
  public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    assertSpring(level, pos, state);
  }

  @Override
  public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onPlace(state, level, pos, oldState, isMoving);
    if (!level.isClientSide) {
      level.scheduleTick(pos, this, state.getValue(TYPE).tickRate);
    }
  }

  private void assertSpring(Level level, BlockPos pos, BlockState state) {
    EnumSpring spring = state.getValue(TYPE);
    level.scheduleTick(pos, this, spring.tickRate);
    Block fluid = spring.liquidBlock.get();

    if (!spring.canGen() || fluid == null) {
      return;
    }

    BlockPos upPos = pos.above();
    if (!level.isEmptyBlock(upPos)) {
      return;
    }

    if (spring.chance != -1 && random.nextInt(spring.chance) != 0) {
      return;
    }

    level.setBlock(upPos, fluid.defaultBlockState(), 3);
  }

  public enum EnumSpring implements StringRepresentable {
    WATER(5, -1, () -> Blocks.WATER),
    OIL(6000, 8, () ->
    {
      if (EnergyConfig.isSpawnOilSprings()) {
        return EnergyFluids.OIL_BLOCK.get();
      }
      return null;
    }); // Set in BuildCraftEnergy

    private final int tickRate, chance;
    private final Supplier<@Nullable Block> liquidBlock;

    EnumSpring(int tickRate, int chance, Supplier<Block> liquidBlock) {
      this.tickRate = tickRate;
      this.chance = chance;
      this.liquidBlock = liquidBlock;
    }

    public int getTickRate() {
      return tickRate;
    }

    public int getChance() {
      return chance;
    }

    public Block getLiquidBlock() {
      return liquidBlock.get();
    }

    public boolean canGen() {
      return this != OIL || EnergyConfig.isSpawnOilSprings();
    }

    @Override
    public String getSerializedName() {
      return name().toLowerCase(Locale.ROOT);
    }
  }
}
