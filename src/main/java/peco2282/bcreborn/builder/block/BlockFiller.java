package peco2282.bcreborn.builder.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.block.BCProperties;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.api.enums.EnumFillerType;
import peco2282.bcreborn.builder.block.entity.BCBuilderBlockEntityTypes;
import peco2282.bcreborn.builder.block.entity.FillerBlockEntity;
import peco2282.bcreborn.lib.block.TileBaseNeptune;

public class BlockFiller extends TileBaseNeptune implements RotatableFacing {
  private static final MapCodec<BlockFiller> CODEC = RecordCodecBuilder
      .mapCodec(instance -> instance.group(
          propertiesCodec(),
          Codec.STRING.fieldOf("id").forGetter(BlockFiller::getId)
      ).apply(instance, BlockFiller::new));

  public BlockFiller(Properties properties, @NotNull String id) {
    super(properties, id,
        new Tuple<>(BCProperties.BLOCK_FACING, Direction.NORTH),
        new Tuple<>(BCProperties.FILLER_TYPE, EnumFillerType.NONE)
    );
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new FillerBlockEntity(pos, state);
  }

  @Override
  protected @NotNull MapCodec<BlockFiller> codec() {
    return CODEC;
  }

  @Override
  protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BCProperties.FILLER_TYPE, BCProperties.BLOCK_FACING);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
    return BaseEntityBlock.createTickerHelper(p_153214_, BCBuilderBlockEntityTypes.FILLER.get(), FillerBlockEntity::tick);
  }
}
