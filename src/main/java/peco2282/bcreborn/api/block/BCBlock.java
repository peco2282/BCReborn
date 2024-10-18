package peco2282.bcreborn.api.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.lib.block.BlockBaseNeptune;
import peco2282.bcreborn.lib.block.TileBaseNeptune;

/**
 * Base interface for BCReborn mod's block.
 * @see BlockBaseNeptune
 * @see TileBaseNeptune
 * @author peco2282
 */
public interface BCBlock {
  String getId();
  default Block getBlock() {
    return (Block) this;
  }

  default Item.Properties itemProperties() {
    return new Item.Properties();
  }
}
