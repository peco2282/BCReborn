package peco2282.bcreborn.lib.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.lib.block.entity.NeptuneBlockEntity;
import peco2282.bcreborn.utils.PropertyBuilder;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public abstract class TileBaseNeptuneBlock extends BaseNeptuneBlock implements EntityBlock, BCBlock {
  public TileBaseNeptuneBlock(Properties properties, @NotNull String id, PropertyBuilder builder) {
    super(properties, id, builder);
  }

  public TileBaseNeptuneBlock(Properties properties, @NotNull String id) {
    super(properties, id, PropertyBuilder.builder());
  }

  protected static <T extends TileBaseNeptuneBlock> MapCodec<T> codecInstance(BiFunction<Properties, String, T> function) {
    return RecordCodecBuilder
        .mapCodec(instance -> instance.group(
            propertiesCodec(),
            Codec.STRING.fieldOf("id").forGetter(BaseNeptuneBlock::getId)
        ).apply(instance, function));
  }

  @NotNull
  @Override
  public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

  @Override
  protected abstract @NotNull MapCodec<? extends TileBaseNeptuneBlock> codec();

  abstract protected void gatherStateDefinition(StateDefinition.Builder<Block, BlockState> builder);

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
    gatherStateDefinition(p_49915_);
  }

  @Override
  public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
    BlockEntity entity = level.getBlockEntity(pos);
    if (entity instanceof NeptuneBlockEntity nep) {
      nep.onExplode(explosion);
    }
    super.onBlockExploded(state, level, pos, explosion);
  }

  @Override
  protected void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
    if (p_60516_.getBlockEntity(p_60517_) instanceof NeptuneBlockEntity nep) {
      nep.onRemove();
    }
    super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
  }

  @Nullable
  protected abstract <E extends BlockEntity> BlockEntityTicker<E> serverTicker(BlockEntityType<E> type);

  @Override
  public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return level.isClientSide() ? null : serverTicker(type);
  }

  @Nullable
  @SuppressWarnings("unchecked")
  protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
      BlockEntityType<A> be1, BlockEntityType<E> be2, BlockEntityTicker<? super E> ticker
  ) {
    return be2 == be1 ? (BlockEntityTicker<A>) ticker : null;
  }
}
