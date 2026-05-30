package com.peco2282.bcreborn.core.block.entity;

import com.peco2282.bcreborn.api.tiles.ITileAreaProvider;
import com.peco2282.bcreborn.common.block.entity.MarkerBlockEntity;
import com.peco2282.bcreborn.core.BlockEntityTypesCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlueMarkerBlockEntity extends MarkerBlockEntity implements ITileAreaProvider {
  public BlueMarkerBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesCore.BLUE_MARKER.get(), pos, state);
  }
}
