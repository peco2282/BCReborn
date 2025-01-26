package peco2282.bcreborn.transport.block.pipe;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface IItemEjector extends PipeBlock {
  /**
   * Ejects an item into a neighboring container.
   *
   * @param stack     The ItemStack to eject.
   * @param direction The direction to eject to.
   */
  void ejectItem(ItemStack stack, Direction direction);
}
