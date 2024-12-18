package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.BasePipeBlock;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.entity.BasePipeBlockEntity;

public class WoddenItemPipeBlockEntity extends BasePipeBlockEntity {
  public WoddenItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.WOODEN_ITEM_PIPE.get(), p_155229_, p_155230_, BasePipeBlock.PipeMaterial.WOOD, BasePipeBlock.PipeType.ITEM);
  }

  public void update(Level level, BlockPos pos, BlockState state) {}

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull WoddenItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }
}
