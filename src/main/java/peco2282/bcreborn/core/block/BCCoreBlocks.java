package peco2282.bcreborn.core.block;

import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.enums.EnumEngineType;
import peco2282.bcreborn.core.item.BCCoreItems;
import peco2282.bcreborn.lib.block.BlockBaseNeptune;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BCCoreBlocks {
  private static final Map<String, String> LANGUAGE = new HashMap<>();
  private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, BCReborn.MODID);
  public static final RegistryObject<BlockSpring> SPRING = register("spring", "Spring", () -> new BlockSpring("spring"));
  public static final RegistryObject<BlockDecoration> DECORATED = register("decorated", "Decorated Block", () -> new BlockDecoration("decorated"));
  public static final RegistryObject<BlockEngine> WOOD_ENGINE = register("wood_engine", "Wooden Engine", () -> new BlockEngine("wood_engine", EnumEngineType.WOOD));
  public static final RegistryObject<BlockEngine> STONE_ENGINE = register("stone_engine", "Stone Engine", () -> new BlockEngine("stone_engine", EnumEngineType.STONE));
  public static final RegistryObject<BlockEngine> IRON_ENGINE = register("iron_engine", "Iron Engine", () -> new BlockEngine("iron_engine", EnumEngineType.IRON));
  public static final RegistryObject<BlockEngine> CREATIVE_ENGINE = register("creative_engine", "Creative Engine", () -> new BlockEngine("creative_engine", EnumEngineType.CREATIVE));

  private static <B extends BlockBaseNeptune> RegistryObject<B> register(String name, String en_name, Supplier<B> block) {
    var ret = REGISTRY.register(name, block);
    LANGUAGE.put(Util.makeDescriptionId("block", BCReborn.location(name)), en_name);
    BCCoreItems.registerBlockItem(name, en_name, ret);
    return ret;
  }

  public static Map<String, String> getLanguage() {
    return LANGUAGE;
  }


  public static void init(IEventBus bus) {
    REGISTRY.register(bus);
  }
}
