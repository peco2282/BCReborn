package peco2282.bcreborn.builder.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCBuilderBlocks {
  public static final RegistryObject<BlockFiller> FILLER = register("filler", () -> new BlockFiller(BlockBehaviour.Properties.of().randomTicks(), "filler"));

  private static <L extends Block & BCBlock> RegistryObject<L> register(String name, Supplier<L> block) {
    return BCRegistry.registerBlockItem(name, block);
  }
}
