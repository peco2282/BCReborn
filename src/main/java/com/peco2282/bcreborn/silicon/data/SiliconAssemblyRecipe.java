/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.silicon.data;

import com.peco2282.bcreborn.transport.gates.ItemGate;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipeBuilder;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipeProvider;
import com.peco2282.bcreborn.core.CoreItems;
import com.peco2282.bcreborn.silicon.SiliconItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import com.peco2282.bcreborn.transport.TransportItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import com.peco2282.bcreborn.transport.TransportBlocks;
import com.peco2282.bcreborn.transport.pipe.PipeMaterial;
import com.peco2282.bcreborn.transport.pipe.PipeType;
import org.jetbrains.annotations.Nullable;

public class SiliconAssemblyRecipe extends AssemblyRecipeProvider {
  public SiliconAssemblyRecipe(PackOutput output) {
    super(output, BCRebornSilicon.MODID);
  }

  @Override
  public void create() {
    // Original TransportSiliconRecipes: dye + redstone dust + iron ingot -> 8 pipe wire, 5000 RF.
    addWireRecipe("red_pipe_wire", Tags.Items.DYES_RED, TransportItems.PIPE_WIRE_RED.get());
    addWireRecipe("blue_pipe_wire", Tags.Items.DYES_BLUE, TransportItems.PIPE_WIRE_BLUE.get());
    addWireRecipe("green_pipe_wire", Tags.Items.DYES_GREEN, TransportItems.PIPE_WIRE_GREEN.get());
    addWireRecipe("yellow_pipe_wire", Tags.Items.DYES_YELLOW, TransportItems.PIPE_WIRE_YELLOW.get());

    // Original TransportSiliconRecipes: 10000 RF each; a plug is made from a cobblestone structure pipe.
    addRecipe(
      AssemblyRecipeBuilder.create(loc("pipe_plug"))
        .addIngredient(new ItemStack(TransportBlocks.get(PipeType.STRUCTURE, PipeMaterial.COBBLESTONE).get()))
        .setEnergy(10000)
        .setResult(new ItemStack(TransportItems.PIPE_PLUG.get(), 8))
    );
    for (DyeColor color : DyeColor.values()) {
      addLensRecipe("pipe_lens_" + color.getName(), color, false);
      addLensRecipe("pipe_filter_" + color.getName(), color, true);
    }
    addLensRecipe("pipe_lens_clear", null, false);
    addLensRecipe("pipe_filter_clear", null, true);

    addRecipe(
      AssemblyRecipeBuilder.create(loc("redstone_chipset"))
        .addIngredient(Items.REDSTONE)
        .setEnergy(100000)
        .setResult(new ItemStack(SiliconItems.REDSTONE_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("iron_chipset"))
        .addIngredient(Items.REDSTONE)
        .addIngredient(Items.IRON_INGOT)
        .setEnergy(200000)
        .setResult(new ItemStack(SiliconItems.IRON_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("gold_chipset"))
        .addIngredient(Items.REDSTONE)
        .addIngredient(Items.GOLD_INGOT)
        .setEnergy(400000)
        .setResult(new ItemStack(SiliconItems.GOLD_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("diamond_chipset"))
        .addIngredient(Items.REDSTONE)
        .addIngredient(Items.DIAMOND)
        .setEnergy(800000)
        .setResult(new ItemStack(SiliconItems.DIAMOND_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("emerald_chipset"))
        .addIngredient(Items.REDSTONE)
        .addIngredient(Items.EMERALD)
        .setEnergy(1200000)
        .setResult(new ItemStack(SiliconItems.EMERALD_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("pulsating_chipset"))
        .addIngredient(Items.REDSTONE)
        .addIngredient(Items.ENDER_PEARL)
        .setEnergy(400000)
        .setResult(new ItemStack(SiliconItems.PULSATING_CHIPSET.get(), 2))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("quartz_chipset"))
        .addIngredient(Items.REDSTONE)
        .addIngredient(Items.QUARTZ)
        .setEnergy(600000)
        .setResult(new ItemStack(SiliconItems.QUARTZ_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("comp_chipset"))
        .addIngredient(Items.REDSTONE)
        .addIngredient(Items.COMPARATOR)
        .setEnergy(600000)
        .setResult(new ItemStack(SiliconItems.COMP_CHIPSET.get()))
    );

    // Original TransportSiliconRecipes: a chipset and one pipe wire per wire colour the gate can emit, at the
    // chipset's own cost. Every gate is made with AND logic; the Integration Table switches it to OR.
    addGateRecipe(GateDefinition.GateMaterial.REDSTONE, SiliconItems.REDSTONE_CHIPSET.get(), 100000);
    addGateRecipe(GateDefinition.GateMaterial.IRON, SiliconItems.IRON_CHIPSET.get(), 200000);
    addGateRecipe(GateDefinition.GateMaterial.GOLD, SiliconItems.GOLD_CHIPSET.get(), 400000);
    addGateRecipe(GateDefinition.GateMaterial.QUARTZ, SiliconItems.QUARTZ_CHIPSET.get(), 600000);
    addGateRecipe(GateDefinition.GateMaterial.DIAMOND, SiliconItems.DIAMOND_CHIPSET.get(), 800000);
    addGateRecipe(GateDefinition.GateMaterial.EMERALD, SiliconItems.EMERALD_CHIPSET.get(), 1200000);

    // Original CoreSiliconRecipes: green dye, redstone and 8 paper make a list (the Debugger has no recipe).
    AssemblyRecipeBuilder list = AssemblyRecipeBuilder.create(loc("list"))
      .addIngredient(Ingredient.of(Tags.Items.DYES_GREEN))
      .addIngredient(Ingredient.of(Tags.Items.DUSTS_REDSTONE));
    for (int i = 0; i < 8; i++) list.addIngredient(Items.PAPER);
    addRecipe(list.setEnergy(20000).setResult(new ItemStack(CoreItems.LIST.get())));

    addRecipe(
      AssemblyRecipeBuilder.create(loc("redstone_crystal"))
        .addIngredient(Items.REDSTONE_BLOCK)
        .setEnergy(10000000)
        .setResult(new ItemStack(SiliconItems.REDSTONE_CRYSTAL.get()))
    );
  }

  /** Dye + glass (+ iron bars for a filter) -> 2 lenses; the clear variants need no dye. */
  private void addLensRecipe(String name, @Nullable DyeColor color, boolean filter) {
    AssemblyRecipeBuilder builder = AssemblyRecipeBuilder.create(loc(name));
    if (color != null) builder.addIngredient(Ingredient.of(color.getTag()));
    builder.addIngredient(Ingredient.of(Tags.Items.GLASS));
    if (filter) builder.addIngredient(Ingredient.of(Items.IRON_BARS));
    addRecipe(builder.setEnergy(10000).setResult(new ItemStack(TransportItems.lens(color, filter), 2)));
  }

  private void addGateRecipe(GateDefinition.GateMaterial material, Item chipset, int energy) {
    Item[] wires = {TransportItems.PIPE_WIRE_RED.get(), TransportItems.PIPE_WIRE_BLUE.get(),
      TransportItems.PIPE_WIRE_GREEN.get(), TransportItems.PIPE_WIRE_YELLOW.get()};
    AssemblyRecipeBuilder builder = AssemblyRecipeBuilder.create(loc(material.getTag() + "_gate")).addIngredient(chipset);
    for (int i = 0; i < material.maxWireColor; i++) {
      builder.addIngredient(wires[i]);
    }
    addRecipe(builder.setEnergy(energy).setResult(ItemGate.makeGateItem(material, GateDefinition.GateLogic.AND)));
  }

  private void addWireRecipe(String name, TagKey<Item> dye, Item wire) {
    addRecipe(
      AssemblyRecipeBuilder.create(loc(name))
        .addIngredient(Ingredient.of(dye))
        .addIngredient(Ingredient.of(Tags.Items.DUSTS_REDSTONE))
        .addIngredient(Ingredient.of(Tags.Items.INGOTS_IRON))
        .setEnergy(5000)
        .setResult(new ItemStack(wire, 8))
    );
  }
}
