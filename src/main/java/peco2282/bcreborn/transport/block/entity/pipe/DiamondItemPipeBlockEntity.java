package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.transport.block.BasePipeBlock;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;

public class DiamondItemPipeBlockEntity extends BasePipeBlockEntity {
  public DiamondItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.DIAMOND_ITEM_PIPE.get(), p_155229_, p_155230_, BasePipeBlock.PipeMaterial.DIAMOND, BasePipeBlock.PipeType.ITEM);
  }
}
