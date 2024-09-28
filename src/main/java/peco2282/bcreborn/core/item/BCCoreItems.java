package peco2282.bcreborn.core.item;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.lib.block.BlockBaseNeptune;
import peco2282.bcreborn.lib.item.BlockItemNeptune;
import peco2282.bcreborn.lib.item.ItemBaseNeptune;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BCCoreItems {
  private static final Map<String, String> LANGUAGE = new HashMap<>();
  private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, BCReborn.MODID);

  public static final RegistryObject<ItemWrench> WRENCH = register("wrench", "Wrench", () -> new ItemWrench("wrench"));

  public static final RegistryObject<ItemGear> GEAR_WOOD = register("gear_wood", "Wood gear", () -> new ItemGear("gear.wood"));
  public static final RegistryObject<ItemGear> GEAR_STONE = register("gear_stone", "Stone gear", () -> new ItemGear("gear.stone"));
  public static final RegistryObject<ItemGear> GEAR_IRON = register("gear_iron", "Iron gear", () -> new ItemGear("gear.iron"));
  public static final RegistryObject<ItemGear> GEAR_GOLD = register("gear_gold", "Gold gear", () -> new ItemGear("gear.gold"));
  public static final RegistryObject<ItemGear> GEAR_DIAMOND = register("gear_diamond", "Diamond gear", () -> new ItemGear("gear.diamond"));

  public static <B extends BlockBaseNeptune> void registerBlockItem(String name, String en_name, Supplier<B> block) {
    LANGUAGE.put(Util.makeDescriptionId(name, BCReborn.location(name)), en_name);
    REGISTRY.register(name, () -> new BlockItemNeptune(block.get(), block.get().itemProperties(), block.get().getId()));
  }

  private static <I extends ItemBaseNeptune> RegistryObject<I> register(String name, String en_name, Supplier<I> item) {
    LANGUAGE.put(Util.makeDescriptionId("item", BCReborn.location(name)), en_name);
    return REGISTRY.register(name, item);
  }

  public static Map<String, String> getLanguage() {
    return LANGUAGE;
  }

  public static void init(IEventBus bus) {
    REGISTRY.register(bus);
  }
}
