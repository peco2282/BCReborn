package peco2282.bcreborn.core.item;

import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.lib.item.ItemBaseNeptune;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCCoreItems {
  public static final RegistryObject<ItemWrench> WRENCH = register("wrench", () -> new ItemWrench("wrench"));
  public static final RegistryObject<ItemGear> GEAR_WOOD = register("gear_wood", () -> new ItemGear("gear.wood"));
  public static final RegistryObject<ItemGear> GEAR_STONE = register("gear_stone", () -> new ItemGear("gear.stone"));
  public static final RegistryObject<ItemGear> GEAR_IRON = register("gear_iron", () -> new ItemGear("gear.iron"));
  public static final RegistryObject<ItemGear> GEAR_GOLD = register("gear_gold", () -> new ItemGear("gear.gold"));
  public static final RegistryObject<ItemGear> GEAR_DIAMOND = register("gear_diamond", () -> new ItemGear("gear.diamond"));

  private static <I extends ItemBaseNeptune> RegistryObject<I> register(String name, Supplier<I> item) {
    return BCRegistry.registerItem(name, item);
  }
  public static void init() {}

}
