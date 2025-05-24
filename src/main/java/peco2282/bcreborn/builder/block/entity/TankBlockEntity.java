package peco2282.bcreborn.builder.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import peco2282.bcreborn.builder.block.TankBlock;
import peco2282.bcreborn.lib.block.entity.BCBaseBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TankBlockEntity extends BCBaseBlockEntity {
  private final List<TankBlock> vertical = new ArrayList<>();
  private final AtomicInteger fluids = new AtomicInteger(0);
  public TankBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCBuilderBlockEntityTypes.TANK.get(), p_155229_, p_155230_);
  }

  public void add(int c) {
    if (fluids.get() + c <= 8) {
      fluids.addAndGet(c);
    } else {
      fluids.set(8);
    }
  }

  public int getFluids() {
    return fluids.get();
  }

  public void total() {
    BlockPos curr = getBlockPos();
    int total = searchDown(Objects.requireNonNull(level), curr, this);
  }

  private static int searchDown(Level level, BlockPos curr, TankBlockEntity self) {
    int total = 0;
    ArrayList<TankBlock> blocks = new ArrayList<>();
    boolean is = self.vertical.isEmpty();
    while (level.getBlockState(curr = curr.below()).getBlock() instanceof TankBlock tank) {
      total += tank.get();
      if (is) {
        blocks.add(tank);
      }
    }
    if (is) {
      self.vertical.addAll(blocks.reversed());
    }
    return total;
  }
}
