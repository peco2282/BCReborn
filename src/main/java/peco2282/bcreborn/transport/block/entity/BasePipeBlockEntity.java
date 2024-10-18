package peco2282.bcreborn.transport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.lib.block.entity.TileNeptune;
import peco2282.bcreborn.transport.block.BaseBlockPipe;

public abstract class BasePipeBlockEntity extends TileNeptune {
  public BasePipeBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
    super(p_155228_, p_155229_, p_155230_);
  }
  public abstract BaseBlockPipe.PipeType getPipeType();
  public abstract BaseBlockPipe.PipeMaterial getPipeMaterial();
}
