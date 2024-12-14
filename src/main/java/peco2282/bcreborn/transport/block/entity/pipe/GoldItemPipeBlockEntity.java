package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;

public class GoldItemPipeBlockEntity extends ItemPipeBlockEntity {
  public GoldItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.GOLD_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.GOLD);
  }
}
