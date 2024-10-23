package peco2282.bcreborn.core.item;

import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.lib.item.BaseNeptuneItem;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCCoreItems {
  public static final RegistryObject<WrenchItem> WRENCH = register("wrench", () -> new WrenchItem("wrench"));
  public static final RegistryObject<GearItem> GEAR_WOOD = register("gear_wood", () -> new GearItem("gear.wood"));
  public static final RegistryObject<GearItem> GEAR_STONE = register("gear_stone", () -> new GearItem("gear.stone"));
  public static final RegistryObject<GearItem> GEAR_IRON = register("gear_iron", () -> new GearItem("gear.iron"));
  public static final RegistryObject<GearItem> GEAR_GOLD = register("gear_gold", () -> new GearItem("gear.gold"));
  public static final RegistryObject<GearItem> GEAR_DIAMOND = register("gear_diamond", () -> new GearItem("gear.diamond"));

  private static <I extends BaseNeptuneItem> RegistryObject<I> register(String name, Supplier<I> item) {
    return BCRegistry.registerItem(name, item);
  }
  public static void init() {}

}
