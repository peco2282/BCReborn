package peco2282.bcreborn.transport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.lib.block.entity.NeptuneBlockEntity;
import peco2282.bcreborn.transport.block.BasePipeBlock;

public abstract class BasePipeBlockEntity extends NeptuneBlockEntity {
  public BasePipeBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
  }
  public abstract BasePipeBlock.PipeType getPipeType();
  public abstract BasePipeBlock.PipeMaterial getPipeMaterial();
}
