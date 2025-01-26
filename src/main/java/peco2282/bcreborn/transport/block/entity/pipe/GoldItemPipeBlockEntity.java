package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;

public class GoldItemPipeBlockEntity extends ItemPipeBlockEntity {
  public GoldItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.GOLD_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.GOLD);
  }

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull GoldItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  @Override
  protected void update(Level level, BlockPos pos, BlockState state) {
  }
}
