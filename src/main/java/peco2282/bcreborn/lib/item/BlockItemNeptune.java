package peco2282.bcreborn.lib.item;

import net.minecraft.world.item.BlockItem;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.item.BCItem;
import peco2282.bcreborn.lib.block.BlockBaseNeptune;

public class BlockItemNeptune extends BlockItem implements BCItem {
  private final String id;

  public BlockItemNeptune(BCBlock p_40565_, Properties p_40566_, String id) {
    super(p_40565_.getBlock(), p_40566_);
    this.id = id;
  }

  @Override
  public String getId() {
    return id;
  }
}
