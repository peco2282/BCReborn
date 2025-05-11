package peco2282.bcreborn.transport.block.pipe;

import io.netty.util.collection.IntObjectMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IItemTransporter extends PipeBlock {
  /**
   * Transports an item to the next pipe or container.
   *
   * @param stack     The ItemStack to transport.
   * @param direction The direction the item came from.
   */
  void transportItem(IntObjectMap<ItemStack> stack, Direction direction);
}
