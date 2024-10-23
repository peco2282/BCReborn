package peco2282.bcreborn.lib.item;

import net.minecraft.world.item.Item;
import peco2282.bcreborn.api.item.BCItem;

public class BaseNeptuneItem extends Item implements BCItem {
  private final String id;
  public BaseNeptuneItem(Properties p_41383_, String id) {
    super(p_41383_);
    this.id = id;
  }

  @Override
  public String getId() {
    if (id.contains(".")) return id.replace(".", "_");
    return id;
  }
}
