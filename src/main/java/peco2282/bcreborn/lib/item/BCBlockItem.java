package peco2282.bcreborn.lib.item;

import net.minecraft.world.item.BlockItem;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.item.BCItem;

public class BCBlockItem extends BlockItem implements BCItem {
  private final String id;

  /**
   * Constructs a new BCBlockItem with the specified block, properties, and ID.
   *
   * @param p_40565_ The block this item is linked to.
   * @param p_40566_ The item properties.
   * @param id       The unique identifier for this BCBlockItem.
   */
  public BCBlockItem(BCBlock p_40565_, Properties p_40566_, String id) {
    super(p_40565_.getBlock(), p_40566_);
    this.id = id;
  }

  /**
   * Retrieves the unique identifier for this BCBlockItem.
   *
   * @return The ID as a String.
   */
  @Override
  public String getId() {
    return id;
  }
}
