package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.pipe.ItemEntity;
import peco2282.bcreborn.transport.block.pipe.TransporterPipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class StoneItemPipeBlockEntity extends ItemPipeBlockEntity implements TransporterPipe {
  public StoneItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.STONE_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.STONE);
  }

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull StoneItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  public void update(Level level, BlockPos pos, BlockState state) {
  }

  @Override
  public List<? extends ItemPipeBlockEntity> targetPipes() {
    return nearPipes(getBlockPos(), getLevel()::getBlockEntity);
  }

  @Override
  public void transportTo(List<ItemEntity> in, ItemPipeBlockEntity pipe) {

  }
  static List<ItemPipeBlockEntity> nearPipes(BlockPos pos, Function<BlockPos, BlockEntity> getter) {
    var nears = new BlockPos[]{pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()};
    var entities = new ArrayList<ItemPipeBlockEntity>();
    for (var near: nears) {
      BlockEntity entity = getter.apply(near);
      if (entity instanceof ItemPipeBlockEntity e) entities.add(e);
    }
    return entities;
  }

  @Override
  public boolean canTransport(List<ItemEntity> in, ItemPipeBlockEntity entity) {
    return true;
  }
}
