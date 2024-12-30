package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.pipe.EmitterPipe;

import java.util.ArrayList;
import java.util.List;

public class WoddenItemPipeBlockEntity extends ItemPipeBlockEntity implements EmitterPipe {
  public WoddenItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.WOODEN_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.WOOD);
  }

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull WoddenItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  public void update(Level level, BlockPos pos, BlockState state) {
  }

  @Override
  public boolean canEmission(Level level, BlockPos from) {
    BlockEntity entity = level.getBlockEntity(from);
    return entity instanceof Container;
  }

  @Override
  public synchronized void emission(Level level, BlockPos from) {
    BlockEntity entity = level.getBlockEntity(from);
    if (entity == null) return;
    Container container = (Container) entity;
    if (container.isEmpty()) return;
    int size = container.getContainerSize();
    List<ItemStack> stacks = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      stacks.add(container.getItem(i));
    }
    // todo: impl(check transportable-item)

    for (int i = 0; i < size; i++) {
      container.setItem(i, stacks.get(i));
    }
  }
}
