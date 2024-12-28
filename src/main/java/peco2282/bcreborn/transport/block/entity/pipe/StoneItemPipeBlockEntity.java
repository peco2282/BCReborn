package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;
import peco2282.bcreborn.transport.block.pipe.ItemEntity;
import peco2282.bcreborn.transport.block.pipe.TransporterPipe;

import java.util.List;

public class StoneItemPipeBlockEntity extends ItemPipeBlockEntity implements TransporterPipe<ItemEntity> {
  public StoneItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.STONE_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.STONE);
  }

  @Override
  public <P extends BasePipeBlockEntity> List<P> targetPipes() {
    return List.of();
  }

  @Override
  public void transportTo(List<ItemEntity> in, BasePipeBlockEntity pipe) {

  }

  @Override
  public boolean canTransport(List<ItemEntity> in, BasePipeBlockEntity entity) {
    return true;
  }
}
