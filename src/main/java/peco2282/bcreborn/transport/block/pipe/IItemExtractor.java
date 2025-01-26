package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IItemExtractor extends PipeBlock {
  /**
   * Extracts an item from a neighboring container.
   *
   * @param direction The direction to extract from.
   * @return The extracted ItemStack.
   */
  ItemStack extractItem(Direction direction);
}
