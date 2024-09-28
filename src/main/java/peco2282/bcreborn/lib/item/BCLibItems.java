package peco2282.bcreborn.lib.item;

import net.minecraft.Util;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.lib.block.BlockBaseNeptune;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BCLibItems {
  private static final Map<String, String> LANGUAGES = new HashMap<>();
  private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, BCReborn.MODID);

//  public static final RegistryObject<ItemGuide> GUIDE = register("guide", () ->
//      new ItemGuide(new Item.Properties(), "guide"));

  public static <B extends BlockBaseNeptune> RegistryObject<BlockItemNeptune> registerBlockItem(String name, String en_name, Supplier<B> block) {
    LANGUAGES.put(Util.makeDescriptionId("item", BCReborn.location(name)), en_name);
    return REGISTRY.register(name, () -> new BlockItemNeptune(block.get(), new Item.Properties(), block.get().getId()));
  }

  private static <I extends Item> RegistryObject<I> register(String name, String en_name, Supplier<I> item) {
    LANGUAGES.put(Util.makeDescriptionId("item", BCReborn.location(name)), en_name);
    return REGISTRY.register(name, item);
  }

  public static Map<String, String> getLanguages() {
    return LANGUAGES;
  }

  public static void init(IEventBus bus) {
    REGISTRY.register(bus);
  }
}
