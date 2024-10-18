package peco2282.bcreborn.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class RegistryUtil {
  public static HolderSet.Named<Block> fromBlockTag(TagKey<Block> key) {
    return BuiltInRegistries.BLOCK.getOrCreateTag(key);
  }

  public static HolderSet.Named<Item> fromItemTag(TagKey<Item> key) {
    return BuiltInRegistries.ITEM.getOrCreateTag(key);
  }

  public static List<Block> flattenBlock(TagKey<Block> key) {
    return fromBlockTag(key).stream().map(Holder::get).toList();
  }
  public static List<Item> flattenItem(TagKey<Item> key) {
    return fromItemTag(key).stream().map(Holder::get).toList();
  }
}
