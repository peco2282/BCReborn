package peco2282.bcreborn.api.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import peco2282.bcreborn.api.Debuggable;
import peco2282.bcreborn.lib.block.BaseNeptuneBlock;
import peco2282.bcreborn.lib.block.TileBaseNeptuneBlock;

/**
 * Base interface for BCReborn mod's block.
 * @see BaseNeptuneBlock
 * @see TileBaseNeptuneBlock
 * @author peco2282
 */
public interface BCBlock extends Debuggable {
  String getId();
  default Block getBlock() {
    return (Block) this;
  }

  default Item.Properties itemProperties() {
    return new Item.Properties();
  }
}
