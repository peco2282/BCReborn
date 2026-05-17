package com.peco2282.bcreborn.common.data.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CommonItemTags {
  private static final String MODID = "bcreborn";

  public static final TagKey<Item> BUILDERS = create("builders");
  public static final TagKey<Item> CORE = create("core");
  public static final TagKey<Item> ENERGY = create("energy");
  public static final TagKey<Item> TRANSPORT = create("transport");

  public static final TagKey<Item> ENGINES = create("engines");

  public static final TagKey<Item> ENGINE_ENERGY = create("engine_energy");

  private static TagKey<Item> create(String name) {
    return ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
  }
}
