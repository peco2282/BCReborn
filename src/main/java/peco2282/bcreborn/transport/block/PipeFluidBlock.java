package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PipeFluidBlock extends BasePipeBlock {
  public PipeFluidBlock(Properties properties, @NotNull String id, PipeMaterial material) {
    this(properties, id, material, PipeType.FLUID);
  }

  private PipeFluidBlock(Properties properties, @NotNull String id, PipeMaterial material, PipeType type) {
    super(properties, id, material, type);
  }

  @Override
  public @NotNull String getId() {
    return "";
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected @NotNull MapCodec<PipeFluidBlock> codec() {
    return codecInstance(PipeFluidBlock::new);
  }
}
