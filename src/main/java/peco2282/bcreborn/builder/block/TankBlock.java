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
import peco2282.bcreborn.lib.block.BCBaseEntityBlock;
import peco2282.bcreborn.utils.PropertyBuilder;

import java.util.concurrent.atomic.AtomicInteger;

public class TankBlock extends BCBaseEntityBlock {
  private final AtomicInteger fluids = new AtomicInteger(8);
  private static final int MAX = 16;
  public TankBlock(Properties properties, @NotNull String id) {
    super(properties, id, PropertyBuilder.builder());
  }

  public int add(int amount) {
    int curr = fluids.get();
    if (curr == MAX) return 0;
    if (curr + amount <= MAX) {
      fluids.addAndGet(amount);
      return amount;
    }

    int plus = curr + amount - MAX;
    fluids.addAndGet(MAX);
    return plus;
  }

  public int get() {
    return fluids.get();
  }

  @Override
  public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return null;
  }

  @Override
  protected @NotNull MapCodec<? extends BCBaseEntityBlock> codec() {
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
