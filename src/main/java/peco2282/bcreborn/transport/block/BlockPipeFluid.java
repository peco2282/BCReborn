package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

public class BlockPipeFluid extends BaseBlockPipe {
  public BlockPipeFluid(Properties properties, @NotNull String id, PipeMaterial material) {
    this(properties, id, material, PipeType.FLUID);
  }

  private BlockPipeFluid(Properties properties, @NotNull String id, PipeMaterial material, PipeType type) {
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
  protected @NotNull MapCodec<BlockPipeFluid> codec() {
    return codecInstance(BlockPipeFluid::new);
  }
}
