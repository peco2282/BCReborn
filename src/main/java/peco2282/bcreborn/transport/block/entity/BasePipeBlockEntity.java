package peco2282.bcreborn.transport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.lib.block.entity.NeptuneBlockEntity;
import peco2282.bcreborn.transport.block.BasePipeBlock;

public abstract class BasePipeBlockEntity extends NeptuneBlockEntity {
  protected final BasePipeBlock.PipeMaterial material;
  protected final BasePipeBlock.PipeType type;
  public BasePipeBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_, BasePipeBlock.PipeMaterial material, BasePipeBlock.PipeType type) {
    super(p_155228_, p_155229_, p_155230_);
    this.material = material;
    this.type = type;
  }

  public final BasePipeBlock.PipeType getPipeType() {
    return type;
  }

  public final BasePipeBlock.PipeMaterial getPipeMaterial() {
    return material;
  }
}
