package com.peco2282.bcreborn.api.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IColoredBlock {
  boolean recolorBlock(
      BlockState state,
      Level level,
      BlockPos pos,
      Direction side,
      DyeColor color
  );
}
