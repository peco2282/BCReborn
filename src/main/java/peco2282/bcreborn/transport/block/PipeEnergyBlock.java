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

public class PipeEnergyBlock extends BasePipeBlock {
  public PipeEnergyBlock(Properties properties, PipeMaterial material, PropertyBuilder builder) {
    super(properties, material, PipeType.ENERGY, builder);
  }

  // for Codec
  private PipeEnergyBlock(Properties properties, PipeMaterial material, PipeType type) {
    super(properties, material, type);
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected @NotNull MapCodec<PipeEnergyBlock> codec() {
    return codecInstance(PipeEnergyBlock::new);
  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(BlockEntityType<E> type) {
    return null;
  }
}
