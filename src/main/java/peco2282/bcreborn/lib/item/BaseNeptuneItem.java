package peco2282.bcreborn.lib.item;

import net.minecraft.world.item.Item;
import peco2282.bcreborn.api.item.BCItem;

/** 
 * Represents a base item used in the Neptune collection 
 * for the Minecraft modding framework. This class adds
 * unique item identification while supporting BCItem functionality.
 *
 * @author peco2282
 */
public class BaseNeptuneItem extends Item implements BCItem {
  private final String id;
  /**
   * Creates a new BaseNeptuneItem instance.
   *
   * @param p_41383_ the item properties used to define behavior and characteristics
   * @param id the unique identifier for the item
   */
  public BaseNeptuneItem(Properties p_41383_, String id) {
    super(p_41383_);
    this.id = id;
  }

  /**
   * Returns the unique identifier for the item.
   * If the ID contains dots, they are replaced with underscores for compatibility.
   *
   * @return the sanitized item ID
   */
  @Override
  public String getId() {
    if (id.contains(".")) return id.replace(".", "_");
    return id;
  }
}
