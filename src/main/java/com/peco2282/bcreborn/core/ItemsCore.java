package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.item.BuildCraftItem;
import com.peco2282.bcreborn.core.item.ListItem;
import com.peco2282.bcreborn.core.item.MapLocationItem;
import com.peco2282.bcreborn.core.item.PaintbrushItem;
import com.peco2282.bcreborn.core.item.WrenchItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornCore.MODID)
public class ItemsCore {
  private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

  public static final RegistryObject<WrenchItem> WRENCH = register("wrench", () -> new WrenchItem(new Item.Properties()));

  public static final RegistryObject<BuildCraftItem> WOODEN_GEAR = register("wooden_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> STONE_GEAR = register("stone_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> IRON_GEAR = register("iron_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> GOLD_GEAR = register("gold_gear", () -> new BuildCraftItem(new Item.Properties()));
  public static final RegistryObject<BuildCraftItem> DIAMOND_GEAR = register("diamond_gear", () -> new BuildCraftItem(new Item.Properties()));

  public static final RegistryObject<BuildCraftItem> BUILD_TOOL_BOX = register("build_tool_box", () -> new BuildCraftItem(new Item.Properties()));


  public static final RegistryObject<ListItem> LIST = register("list", ListItem::new);
  public static final RegistryObject<PaintbrushItem> PAINTBRUSH = register("paintbrush", PaintbrushItem::new);
  public static final RegistryObject<MapLocationItem> MAP_LOCATION = register("map_location", MapLocationItem::new);


  private static <I extends Item> RegistryObject<I> register(String name, Supplier<I> item) {
    return REGISTRY.registerItem(name, item);
  }
}
