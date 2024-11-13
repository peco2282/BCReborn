package peco2282.bcreborn.builder.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.lib.block.entity.NeptuneBlockEntity;

public class TankBlockEntity extends NeptuneBlockEntity {
  public TankBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCBuilderBlockEntityTypes.TANK.get(), p_155229_, p_155230_);
  }
}