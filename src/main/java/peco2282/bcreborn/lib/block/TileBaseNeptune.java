package peco2282.bcreborn.lib.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.block.RotatableFacing;
import peco2282.bcreborn.lib.block.entity.TileNeptune;

public abstract class TileBaseNeptune extends BlockBaseNeptune implements EntityBlock {
  @SafeVarargs
  public TileBaseNeptune(Properties properties, @NotNull String id, Tuple<Property<?>, ?>... states) {
    super(properties, id, states);
  }

  @NotNull
  @Override
  public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

  @Override
  protected abstract @NotNull MapCodec<? extends Block> codec();

  @Override
  public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
    BlockEntity entity = level.getBlockEntity(pos);
    if (entity instanceof TileNeptune nep) {
      nep.onExplode(explosion);
    }
    super.onBlockExploded(state, level, pos, explosion);
  }

  @Override
  protected void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
    if (p_60516_.getBlockEntity(p_60517_) instanceof TileNeptune nep) {
      nep.onRemove();
    }
    super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
  }
}
