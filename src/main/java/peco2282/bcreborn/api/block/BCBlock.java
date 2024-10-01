package peco2282.bcreborn.api.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface BCBlock {
  String getId();
  default Block getBlock() {
    return (Block) this;
  }

  default Item.Properties itemProperties() {
    return new Item.Properties();
  }
}
