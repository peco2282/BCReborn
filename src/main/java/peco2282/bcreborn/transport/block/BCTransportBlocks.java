package peco2282.bcreborn.transport.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

public class BCTransportBlocks {
  // Wood
  public static final RegistryObject<BaseBlockPipe> WOOD_ITEM_PIPE = register("wooden_item_pipe", () -> new BlockPipeItem(Properties.of(), "", BaseBlockPipe.PipeMaterial.WOOD));
  public static final RegistryObject<BaseBlockPipe> WOOD_FLUID_PIPE = register("wooden_fluid_pipe", () -> new BlockPipeFluid(Properties.of(), "", BaseBlockPipe.PipeMaterial.WOOD));
  public static final RegistryObject<BaseBlockPipe> WOOD_ENERGY_PIPE = register("wooden_energy_pipe", () -> new BlockPipeEnergy(Properties.of(), "", BaseBlockPipe.PipeMaterial.WOOD));
  // Stone
  public static final RegistryObject<BaseBlockPipe> STONE_ITEM_PIPE = register("stone_item_pipe", () -> new BlockPipeItem(Properties.of(), "", BaseBlockPipe.PipeMaterial.STONE));
  public static final RegistryObject<BaseBlockPipe> STONE_FLUID_PIPE = register("stone_fluid_pipe", () -> new BlockPipeFluid(Properties.of(), "", BaseBlockPipe.PipeMaterial.STONE));
  public static final RegistryObject<BaseBlockPipe> STONE_ENERGY_PIPE = register("stone_energy_pipe", () -> new BlockPipeEnergy(Properties.of(), "", BaseBlockPipe.PipeMaterial.STONE));
  // CobbleStone
  public static final RegistryObject<BaseBlockPipe> COBBLESTONE_ITEM_PIPE = register("cobblestone_item_pipe", () -> new BlockPipeItem(Properties.of(), "", BaseBlockPipe.PipeMaterial.COBBLESTONE));
  public static final RegistryObject<BaseBlockPipe> COBBLESTONE_FLUID_PIPE = register("cobblestone_fluid_pipe", () -> new BlockPipeFluid(Properties.of(), "", BaseBlockPipe.PipeMaterial.COBBLESTONE));
  public static final RegistryObject<BaseBlockPipe> COBBLESTONE_ENERGY_PIPE = register("cobblestone_energy_pipe", () -> new BlockPipeEnergy(Properties.of(), "", BaseBlockPipe.PipeMaterial.COBBLESTONE));
  // Iron
  public static final RegistryObject<BaseBlockPipe> IRON_ITEM_PIPE = register("iron_item_pipe", () -> new BlockPipeItem(Properties.of(), "", BaseBlockPipe.PipeMaterial.IRON));
  public static final RegistryObject<BaseBlockPipe> IRON_FLUID_PIPE = register("iron_fluid_pipe", () -> new BlockPipeFluid(Properties.of(), "", BaseBlockPipe.PipeMaterial.IRON));
  public static final RegistryObject<BaseBlockPipe> IRON_ENERGY_PIPE = register("iron_energy_pipe", () -> new BlockPipeEnergy(Properties.of(), "", BaseBlockPipe.PipeMaterial.IRON));
  // Gold
  public static final RegistryObject<BaseBlockPipe> GOLD_ITEM_PIPE = register("gold_item_pipe", () -> new BlockPipeItem(Properties.of(), "", BaseBlockPipe.PipeMaterial.GOLD));
  public static final RegistryObject<BaseBlockPipe> GOLD_FLUID_PIPE = register("gold_fluid_pipe", () -> new BlockPipeFluid(Properties.of(), "", BaseBlockPipe.PipeMaterial.GOLD));
  public static final RegistryObject<BaseBlockPipe> GOLD_ENERGY_PIPE = register("gold_energy_pipe", () -> new BlockPipeEnergy(Properties.of(), "", BaseBlockPipe.PipeMaterial.GOLD));
  // Diamond
  public static final RegistryObject<BaseBlockPipe> DIAMOND_ITEM_PIPE = register("diamond_item_pipe", () -> new BlockPipeItem(Properties.of(), "", BaseBlockPipe.PipeMaterial.DIAMOND));
  public static final RegistryObject<BaseBlockPipe> DIAMOND_FLUID_PIPE = register("diamond_fluid_pipe", () -> new BlockPipeFluid(Properties.of(), "", BaseBlockPipe.PipeMaterial.DIAMOND));
  public static final RegistryObject<BaseBlockPipe> DIAMOND_ENERGY_PIPE = register("diamond_energy_pipe", () -> new BlockPipeEnergy(Properties.of(), "", BaseBlockPipe.PipeMaterial.DIAMOND));

  private static <B extends Block & BCBlock> RegistryObject<B> register(String name, Supplier<B> block) {
    return BCRegistry.registerBlockItem(name, block);
  }
  public static void init() {}
}
