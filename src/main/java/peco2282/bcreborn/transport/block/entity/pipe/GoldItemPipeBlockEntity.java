package peco2282.bcreborn.transport.block.entity.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.transport.block.PipeMaterial;
import peco2282.bcreborn.transport.block.entity.BCTransportBlockEntities;
import peco2282.bcreborn.transport.block.pipe.IItemEjector;
import peco2282.bcreborn.transport.block.pipe.IItemTransporter;

public class GoldItemPipeBlockEntity extends ItemPipeBlockEntity implements IItemTransporter, IItemEjector {
  public GoldItemPipeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BCTransportBlockEntities.GOLD_ITEM_PIPE.get(), p_155229_, p_155230_, PipeMaterial.GOLD);
  }

  @Contract(pure = true)
  public static void tick(Level world, BlockPos pos, BlockState state, @NotNull GoldItemPipeBlockEntity blockEntity) {
    blockEntity.update(world, pos, state);
  }

  @Override
  protected void update(Level level, BlockPos pos, BlockState state) {
  }

  /**
   * Transports an item to the next pipe or container.
   *
   * @param stack     The storage polled ItemStack to transport.
   * @param direction The direction the item came from.
   */
  @Override
  public void transportItem(ItemStack stack, Direction direction) {
    for (Direction dir : Direction.values()) {
      BlockPos nextPos = getBlockPos().relative(dir);
      BlockEntity nextEntity = getLevel().getBlockEntity(nextPos);
      if (nextEntity instanceof IItemTransporter) {
        ((IItemTransporter) nextEntity).transportItem(stack, dir);
      } else if (nextEntity instanceof IItemEjector) {
        ((IItemEjector) nextEntity).ejectItem(stack, dir);
      }
    }
  }

  /**
   * Ejects an item into a neighboring container.
   *
   * @param stack     The ItemStack to eject.
   * @param direction The direction to eject to.
   */
  @Override
  public void ejectItem(ItemStack stack, Direction direction) {
    BlockPos targetPos = getBlockPos().relative(direction);
    BlockEntity target = getLevel().getBlockEntity(targetPos);
    if (target instanceof Container inventory) {
      for (int i = 0; i < inventory.getContainerSize(); i++) {
        if (inventory.canPlaceItem(i, stack)) {
          inventory.setItem(i, stack);
          break;
        }
      }
    }
  }
}
