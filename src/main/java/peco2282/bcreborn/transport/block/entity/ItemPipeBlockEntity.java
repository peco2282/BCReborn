package peco2282.bcreborn.transport.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.transport.block.BaseBlockPipe;

public class ItemPipeBlockEntity extends BasePipeBlockEntity {
  private final BaseBlockPipe.PipeMaterial material;
  public ItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_, BaseBlockPipe.PipeMaterial material) {
    super(BCTransportBlockEntities.ITEM_PIPE.get(), p_155229_, p_155230_);
    this.material = material;
  }

  private void update(BlockPos pos, BlockState state) {}

  public static void tick(Level world, BlockPos pos, BlockState state, ItemPipeBlockEntity blockEntity) {
    blockEntity.update(pos, state);
  }

  @Override
  public BaseBlockPipe.PipeType getPipeType() {
    return BaseBlockPipe.PipeType.ITEM;
  }

  @Override
  public BaseBlockPipe.PipeMaterial getPipeMaterial() {
    return this.material;
  }
}
