package peco2282.bcreborn.builder.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.lib.block.TileBaseNeptuneBlock;
import peco2282.bcreborn.utils.PropertyBuilder;

public class TankBlock extends TileBaseNeptuneBlock {
  public TankBlock(Properties properties, @NotNull String id) {
    super(properties, id, PropertyBuilder.builder());
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected @NotNull MapCodec<? extends TileBaseNeptuneBlock> codec() {
    return null;
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

  }

  @Override
  protected @Nullable <E extends BlockEntity> BlockEntityTicker<E> serverTicker(BlockEntityType<E> type) {
    return null;
  }
}
