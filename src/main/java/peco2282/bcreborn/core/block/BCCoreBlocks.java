package peco2282.bcreborn.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.enums.EnumEngineType;
import peco2282.bcreborn.core.fluid.BCCoreFluids;
import peco2282.bcreborn.core.fluid.ExplosibleLiquid;
import peco2282.bcreborn.lib.item.BlockItemNeptune;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCCoreBlocks {
  public static final RegistryObject<BlockSpring> SPRING = register("spring", () -> new BlockSpring("spring"));
  public static final RegistryObject<BlockDecoration> DECORATED = register("decorated", () -> new BlockDecoration("decorated"));
  public static final RegistryObject<BlockEngine> WOOD_ENGINE = register("wood_engine", () -> new BlockEngine("wood_engine", EnumEngineType.WOOD));
  public static final RegistryObject<BlockEngine> STONE_ENGINE = register("stone_engine", () -> new BlockEngine("stone_engine", EnumEngineType.STONE));
  public static final RegistryObject<BlockEngine> IRON_ENGINE = register("iron_engine", () -> new BlockEngine("iron_engine", EnumEngineType.IRON));
  public static final RegistryObject<BlockEngine> CREATIVE_ENGINE = register("creative_engine", () -> new BlockEngine("creative_engine", EnumEngineType.CREATIVE));

  public static final RegistryObject<ExplosibleLiquid> OIL = register("oil", () -> new ExplosibleLiquid(BCCoreFluids.OIL_SOURCE, BlockBehaviour.Properties.of().liquid().noCollission().noLootTable(), "oil_source"));
  public static final RegistryObject<ExplosibleLiquid> FUEL = register("fuel", () -> new ExplosibleLiquid(BCCoreFluids.FUEL_SOURCE, BlockBehaviour.Properties.of().liquid().noCollission().noLootTable(), "fuel_source"));

  private static <B extends Block & BCBlock> RegistryObject<B> register(String name, Supplier<B> block) {
    return BCRegistry.registerBlockItem(name, block, BlockItemNeptune::new);
  }

  public static void init() {
  }

}
