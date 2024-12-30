package peco2282.bcreborn.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class RegistryUtil {
  /**
   * Retrieves a named holder set of blocks matching the specified tag key.
   *
   * @param key The tag key for the blocks.
   * @return A named {@link HolderSet} of blocks associated with the tag.
   */
  public static HolderSet.Named<Block> fromBlockTag(TagKey<Block> key) {
    return BuiltInRegistries.BLOCK.getOrCreateTag(key);
  }

  /**
   * Retrieves a named holder set of items matching the specified tag key.
   *
   * @param key The tag key for the items.
   * @return A named {@link HolderSet} of items associated with the tag.
   */
  public static HolderSet.Named<Item> fromItemTag(TagKey<Item> key) {
    return BuiltInRegistries.ITEM.getOrCreateTag(key);
  }

  /**
   * Converts all blocks from a tag key into a flat list of {@link Block} objects.
   *
   * @param key The tag key for the blocks.
   * @return A list of {@link Block} objects contained in the tag.
   */
  public static List<Block> flattenBlock(TagKey<Block> key) {
    return fromBlockTag(key).stream().map(Holder::get).toList();
  }
  /**
   * Converts all items from a tag key into a flat list of {@link Item} objects.
   *
   * @param key The tag key for the items.
   * @return A list of {@link Item} objects contained in the tag.
   */
  public static List<Item> flattenItem(TagKey<Item> key) {
    return fromItemTag(key).stream().map(Holder::get).toList();
  }
}
