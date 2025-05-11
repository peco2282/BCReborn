package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.utils.PropertyBuilder;

public class PipeFluidBlock extends BasePipeBlock {
  public PipeFluidBlock(Properties properties, PipeMaterial material, PropertyBuilder builder) {
    super(properties, material, PipeType.FLUID, builder);
  }

  // for Codec
  private PipeFluidBlock(Properties properties, PipeMaterial material, PipeType type) {
    super(properties, material, type);
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected @NotNull MapCodec<PipeFluidBlock> codec() {
    return codecInstance(PipeFluidBlock::new);
  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(BlockEntityType<E> type) {
    return null;
  }
}
