package peco2282.bcreborn.data.tag;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import peco2282.bcreborn.BCReborn;

@SuppressWarnings("SameParameterValue")
public class BCItemTag {
  public static final TagKey<Item> GEAR = create("gear");

  private static TagKey<Item> create(String name) {
    return ItemTags.create(BCReborn.MODID, name);
  }
}
