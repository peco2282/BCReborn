package peco2282.bcreborn.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.api.enums.EnumEngineType;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.core.fluid.BCCoreFluids;
import peco2282.bcreborn.core.fluid.ExplosibleLiquid;
import peco2282.bcreborn.lib.item.BlockItemNeptune;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCCoreBlocks {
  public static final RegistryObject<SpringBlock> SPRING = register("spring", () -> new SpringBlock("spring"));
  public static final RegistryObject<DecorationBlock> DECORATED = register("decorated", () -> new DecorationBlock("decorated"));
  public static final RegistryObject<EngineBlock> WOOD_ENGINE = register("wood_engine", () -> new EngineBlock("wood_engine", EnumEngineType.WOOD));
  public static final RegistryObject<EngineBlock> STONE_ENGINE = register("stone_engine", () -> new EngineBlock("stone_engine", EnumEngineType.STONE));
  public static final RegistryObject<EngineBlock> IRON_ENGINE = register("iron_engine", () -> new EngineBlock("iron_engine", EnumEngineType.IRON));
  public static final RegistryObject<EngineBlock> CREATIVE_ENGINE = register("creative_engine", () -> new EngineBlock("creative_engine", EnumEngineType.CREATIVE));

  public static final RegistryObject<MarkerVolumeBlock> MARKER_VOLUME = register("marker_volume", () -> new MarkerVolumeBlock(BlockBehaviour.Properties.of(), "marker_volume"));

  public static final RegistryObject<ExplosibleLiquid> OIL_SOURCE = register("oil", () -> new ExplosibleLiquid(BCCoreFluids.OIL_SOURCE, BlockBehaviour.Properties.of().liquid().noCollission().noLootTable(), "oil_source"));
//  public static final RegistryObject<ExplosibleLiquid> OIL_FLOWING = register("oil_flowing", () -> new ExplosibleLiquid(BCCoreFluids.OIL_FLOWING, BlockBehaviour.Properties.of().liquid().noCollission().noLootTable(), "oil_flowing"));
  public static final RegistryObject<ExplosibleLiquid> FUEL_SOURCE = register("fuel", () -> new ExplosibleLiquid(BCCoreFluids.FUEL_SOURCE, BlockBehaviour.Properties.of().liquid().noCollission().noLootTable(), "fuel_source"));
//  public static final RegistryObject<ExplosibleLiquid> FUEL_FLOWING = register("fuel_flowing", () -> new ExplosibleLiquid(BCCoreFluids.FUEL_FLOWING, BlockBehaviour.Properties.of().liquid().noCollission().noLootTable(), "fuel_flowing"));

  private static <B extends Block & BCBlock> RegistryObject<B> register(String name, Supplier<B> block) {
    return BCRegistry.registerBlockItem(name, block, BlockItemNeptune::new);
  }
}
