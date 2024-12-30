package peco2282.bcreborn.transport.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.RegistryObject;
import peco2282.bcreborn.api.block.BCBlock;
import peco2282.bcreborn.bean.InitRegister;
import peco2282.bcreborn.registry.BCRegistry;

import java.util.function.Supplier;

@InitRegister
public class BCTransportBlocks {
  // Wood
  public static final RegistryObject<BasePipeBlock> WOOD_ITEM_PIPE = register("wooden_item_pipe", () -> new PipeItemBlock(Properties.of(), "", PipeMaterial.WOOD));
  public static final RegistryObject<BasePipeBlock> WOOD_FLUID_PIPE = register("wooden_fluid_pipe", () -> new PipeFluidBlock(Properties.of(), "", PipeMaterial.WOOD));
  public static final RegistryObject<BasePipeBlock> WOOD_ENERGY_PIPE = register("wooden_energy_pipe", () -> new PipeEnergyBlock(Properties.of(), "", PipeMaterial.WOOD));
  // Stone
  public static final RegistryObject<BasePipeBlock> STONE_ITEM_PIPE = register("stone_item_pipe", () -> new PipeItemBlock(Properties.of(), "", PipeMaterial.STONE));
  public static final RegistryObject<BasePipeBlock> STONE_FLUID_PIPE = register("stone_fluid_pipe", () -> new PipeFluidBlock(Properties.of(), "", PipeMaterial.STONE));
  public static final RegistryObject<BasePipeBlock> STONE_ENERGY_PIPE = register("stone_energy_pipe", () -> new PipeEnergyBlock(Properties.of(), "", PipeMaterial.STONE));
  // CobbleStone
  public static final RegistryObject<BasePipeBlock> COBBLESTONE_ITEM_PIPE = register("cobblestone_item_pipe", () -> new PipeItemBlock(Properties.of(), "", PipeMaterial.COBBLESTONE));
  public static final RegistryObject<BasePipeBlock> COBBLESTONE_FLUID_PIPE = register("cobblestone_fluid_pipe", () -> new PipeFluidBlock(Properties.of(), "", PipeMaterial.COBBLESTONE));
  public static final RegistryObject<BasePipeBlock> COBBLESTONE_ENERGY_PIPE = register("cobblestone_energy_pipe", () -> new PipeEnergyBlock(Properties.of(), "", PipeMaterial.COBBLESTONE));
  // Iron
  public static final RegistryObject<BasePipeBlock> IRON_ITEM_PIPE = register("iron_item_pipe", () -> new PipeItemBlock(Properties.of(), "", PipeMaterial.IRON));
  public static final RegistryObject<BasePipeBlock> IRON_FLUID_PIPE = register("iron_fluid_pipe", () -> new PipeFluidBlock(Properties.of(), "", PipeMaterial.IRON));
  public static final RegistryObject<BasePipeBlock> IRON_ENERGY_PIPE = register("iron_energy_pipe", () -> new PipeEnergyBlock(Properties.of(), "", PipeMaterial.IRON));
  // Gold
  public static final RegistryObject<BasePipeBlock> GOLD_ITEM_PIPE = register("gold_item_pipe", () -> new PipeItemBlock(Properties.of(), "", PipeMaterial.GOLD));
  public static final RegistryObject<BasePipeBlock> GOLD_FLUID_PIPE = register("gold_fluid_pipe", () -> new PipeFluidBlock(Properties.of(), "", PipeMaterial.GOLD));
  public static final RegistryObject<BasePipeBlock> GOLD_ENERGY_PIPE = register("gold_energy_pipe", () -> new PipeEnergyBlock(Properties.of(), "", PipeMaterial.GOLD));
  // Diamond
  public static final RegistryObject<BasePipeBlock> DIAMOND_ITEM_PIPE = register("diamond_item_pipe", () -> new PipeItemBlock(Properties.of(), "", PipeMaterial.DIAMOND));
  public static final RegistryObject<BasePipeBlock> DIAMOND_FLUID_PIPE = register("diamond_fluid_pipe", () -> new PipeFluidBlock(Properties.of(), "", PipeMaterial.DIAMOND));
  public static final RegistryObject<BasePipeBlock> DIAMOND_ENERGY_PIPE = register("diamond_energy_pipe", () -> new PipeEnergyBlock(Properties.of(), "", PipeMaterial.DIAMOND));

  private static <B extends Block & BCBlock> RegistryObject<B> register(String name, Supplier<B> block) {
    return BCRegistry.registerBlockItem(name, block);
  }
}
