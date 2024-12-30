package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.pipe.ItemEntity;
import peco2282.bcreborn.transport.block.pipe.PipeFilter;

import java.util.List;

public class DiamondItemPipeBlockEntity extends ItemPipeBlockEntity implements PipeFilter<ItemEntity> {
  public DiamondItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.DIAMOND_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.DIAMOND);
  }

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull DiamondItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  public void update(Level level, BlockPos pos, BlockState state) {
  }

  @Override
  public int size() {
    return 6;
  }

  @Override
  public boolean accept(List<ItemEntity> entity) {
    return true;
  }

  @Override
  public List<List<ItemEntity>> getAll() {
    return List.of();
  }
}
