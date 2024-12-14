package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.PipeType;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;
import peco2282.bcreborn.transport.logic.PipeStorage;

public abstract class FluidPipeBlockEntity extends BasePipeBlockEntity {
  public FluidPipeBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_, PipeMaterial material) {
    super(p_155228_, p_155229_, p_155230_, material, PipeType.FLUID);
  }

  @Override
  public PipeStorage.FluidStorage getStorage() {
    return this.storage.asFluidStorage();
  }
}
