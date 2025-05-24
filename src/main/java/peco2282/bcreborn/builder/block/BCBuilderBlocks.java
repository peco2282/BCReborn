package peco2282.bcreborn.builder.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCBuilderBlocks {
  public static final RegistryObject<FillerBlock> FILLER = register("filler", () -> new FillerBlock(BlockBehaviour.Properties.of().randomTicks(), "filler"));
  public static final RegistryObject<QuarryBlock> QUARRY = register("quarry", () -> new QuarryBlock(BlockBehaviour.Properties.of().randomTicks(), "quarry"));
  public static final RegistryObject<ChuteBlock> IRON_HOPPER = register("chute", () -> new ChuteBlock(BlockBehaviour.Properties.of().randomTicks(), "iron_hopper"));
  public static final RegistryObject<TankBlock> TANK = register("tank", () -> new TankBlock(BlockBehaviour.Properties.of().randomTicks(), "tank"));

  private static <L extends Block & BCBlock> RegistryObject<L> register(String name, Supplier<L> block) {
    return BCRegistry.registerBlockItem(name, block);
  }
}
