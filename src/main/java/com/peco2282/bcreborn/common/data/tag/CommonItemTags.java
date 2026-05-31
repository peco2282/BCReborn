/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
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
  public static final TagKey<Item> FACTORY = create("factory");
  public static final TagKey<Item> ROBOTICS = create("robotics");
  public static final TagKey<Item> SILICON = create("silicon");
  public static final TagKey<Item> TRANSPORT = create("transport");

  public static final TagKey<Item> ENGINES = create("engines");
  public static final TagKey<Item> GEAR = create("gear");
  public static final TagKey<Item> FUEL_BUCKET = create("fuel_bucket");

  public static final TagKey<Item> ENGINE_ENERGY = create("engine_energy");

  public static final TagKey<Item> CHIPSET = create("chipset");

  private static TagKey<Item> create(String name) {
    return ItemTags.create(ResourceLocation.fromNamespaceAndPath(MODID, name));
  }
}
