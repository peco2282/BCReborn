/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
  public static final RegistryObject<BCBasePipeBlock> WOOD_ITEM_PIPE =
      register(
          "wooden_item_pipe",
          () -> new PipeItemBlock(Properties.of(), PipeMaterial.WOOD, PipeProperties.WOODEN_ITEM));
  public static final RegistryObject<BCBasePipeBlock> WOOD_FLUID_PIPE =
      register(
          "wooden_fluid_pipe",
          () ->
              new PipeFluidBlock(Properties.of(), PipeMaterial.WOOD, PipeProperties.WOODEN_FLUID));
  public static final RegistryObject<BCBasePipeBlock> WOOD_ENERGY_PIPE =
      register(
          "wooden_energy_pipe",
          () ->
              new PipeEnergyBlock(
                  Properties.of(), PipeMaterial.WOOD, PipeProperties.WOODEN_ENERGY));
  // Stone
  public static final RegistryObject<BCBasePipeBlock> STONE_ITEM_PIPE =
      register(
          "stone_item_pipe",
          () -> new PipeItemBlock(Properties.of(), PipeMaterial.STONE, PipeProperties.STONE_ITEM));
  public static final RegistryObject<BCBasePipeBlock> STONE_FLUID_PIPE =
      register(
          "stone_fluid_pipe",
          () ->
              new PipeFluidBlock(Properties.of(), PipeMaterial.STONE, PipeProperties.STONE_FLUID));
  public static final RegistryObject<BCBasePipeBlock> STONE_ENERGY_PIPE =
      register(
          "stone_energy_pipe",
          () ->
              new PipeEnergyBlock(
                  Properties.of(), PipeMaterial.STONE, PipeProperties.STONE_ENERGY));
  // CobbleStone
  public static final RegistryObject<BCBasePipeBlock> COBBLESTONE_ITEM_PIPE =
      register(
          "cobblestone_item_pipe",
          () ->
              new PipeItemBlock(
                  Properties.of(), PipeMaterial.COBBLESTONE, PipeProperties.COBBLESTONE_ITEM));
  public static final RegistryObject<BCBasePipeBlock> COBBLESTONE_FLUID_PIPE =
      register(
          "cobblestone_fluid_pipe",
          () ->
              new PipeFluidBlock(
                  Properties.of(), PipeMaterial.COBBLESTONE, PipeProperties.COBBLESTONE_FLUID));
  public static final RegistryObject<BCBasePipeBlock> COBBLESTONE_ENERGY_PIPE =
      register(
          "cobblestone_energy_pipe",
          () ->
              new PipeEnergyBlock(
                  Properties.of(), PipeMaterial.COBBLESTONE, PipeProperties.COBBLESTONE_ENERGY));
  // Iron
  public static final RegistryObject<BCBasePipeBlock> IRON_ITEM_PIPE =
      register(
          "iron_item_pipe",
          () -> new PipeItemBlock(Properties.of(), PipeMaterial.IRON, PipeProperties.IRON_ITEM));
  public static final RegistryObject<BCBasePipeBlock> IRON_FLUID_PIPE =
      register(
          "iron_fluid_pipe",
          () -> new PipeFluidBlock(Properties.of(), PipeMaterial.IRON, PipeProperties.IRON_FLUID));
  public static final RegistryObject<BCBasePipeBlock> IRON_ENERGY_PIPE =
      register(
          "iron_energy_pipe",
          () ->
              new PipeEnergyBlock(Properties.of(), PipeMaterial.IRON, PipeProperties.IRON_ENERGY));
  // Gold
  public static final RegistryObject<BCBasePipeBlock> GOLD_ITEM_PIPE =
      register(
          "gold_item_pipe",
          () -> new PipeItemBlock(Properties.of(), PipeMaterial.GOLD, PipeProperties.GOLD_ITEM));
  public static final RegistryObject<BCBasePipeBlock> GOLD_FLUID_PIPE =
      register(
          "gold_fluid_pipe",
          () -> new PipeFluidBlock(Properties.of(), PipeMaterial.GOLD, PipeProperties.GOLD_FLUID));
  public static final RegistryObject<BCBasePipeBlock> GOLD_ENERGY_PIPE =
      register(
          "gold_energy_pipe",
          () ->
              new PipeEnergyBlock(Properties.of(), PipeMaterial.GOLD, PipeProperties.GOLD_ENERGY));
  // Diamond
  public static final RegistryObject<BCBasePipeBlock> DIAMOND_ITEM_PIPE =
      register(
          "diamond_item_pipe",
          () ->
              new PipeItemBlock(
                  Properties.of(), PipeMaterial.DIAMOND, PipeProperties.DIAMOND_ITEM));
  public static final RegistryObject<BCBasePipeBlock> DIAMOND_FLUID_PIPE =
      register(
          "diamond_fluid_pipe",
          () ->
              new PipeFluidBlock(
                  Properties.of(), PipeMaterial.DIAMOND, PipeProperties.DIAMOND_FLUID));
  public static final RegistryObject<BCBasePipeBlock> DIAMOND_ENERGY_PIPE =
      register(
          "diamond_energy_pipe",
          () ->
              new PipeEnergyBlock(
                  Properties.of(), PipeMaterial.DIAMOND, PipeProperties.DIAMOND_ENERGY));

  private static <B extends Block & BCBlock> RegistryObject<B> register(
      String name, Supplier<B> block) {
    return BCRegistry.registerBlockItem(name, block);
  }
}
