package peco2282.bcreborn.lib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.api.block.BCBlockEntity;

public abstract class TileContainerNeptune extends BaseContainerBlockEntity implements BCBlockEntity {
  protected TileContainerNeptune(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }
}
