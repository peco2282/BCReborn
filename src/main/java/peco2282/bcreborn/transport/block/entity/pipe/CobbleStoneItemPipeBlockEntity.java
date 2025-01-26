package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;

public class CobbleStoneItemPipeBlockEntity extends ItemPipeBlockEntity {
  public CobbleStoneItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230) {
    super(BCTransportBlockEntities.COBBLESTONE_ITEM_PIPE.get(), p_155229_, p_155230, PipeMaterial.STONE);
  }

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull CobbleStoneItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  @Override
  protected void update(Level level, BlockPos pos, BlockState state) {
  }
}
