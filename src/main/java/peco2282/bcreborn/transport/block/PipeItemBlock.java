package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.transport.block.pipe.PipeMaterialHandler;

public class PipeItemBlock extends BasePipeBlock {
  private final PipeMaterialHandler HANDLER = new PipeMaterialHandler(getPipeMaterial());

  public PipeItemBlock(Properties properties, @NotNull String id, PipeMaterial material) {
    this(properties, id, material, PipeType.ITEM);
  }

  private PipeItemBlock(Properties properties, @NotNull String id, PipeMaterial material, PipeType type) {
    super(properties, id, material, type);
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return HANDLER.newBlockEntity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<PipeItemBlock> codec() {
    return codecInstance(PipeItemBlock::new);
  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(BlockEntityType<E> type) {
    return HANDLER.serverTicker(type, PipeItemBlock::createTickerHelper);
  }
}
