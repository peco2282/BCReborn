package peco2282.bcreborn.transport.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PipeEnergyBlock extends BasePipeBlock {
  public PipeEnergyBlock(Properties properties, @NotNull String id, PipeMaterial material) {
    this(properties, id, material, PipeType.ENERGY);
  }

  private PipeEnergyBlock(Properties properties, @NotNull String id, PipeMaterial material, PipeType type) {
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
  protected @NotNull MapCodec<PipeEnergyBlock> codec() {
    return codecInstance(PipeEnergyBlock::new);
  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(BlockEntityType<E> type) {
    return null;
  }
}
