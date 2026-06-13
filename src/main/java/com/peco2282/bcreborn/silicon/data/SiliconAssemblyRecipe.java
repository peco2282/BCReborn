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

import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipeBuilder;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipeProvider;
import com.peco2282.bcreborn.silicon.SiliconItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SiliconAssemblyRecipe extends AssemblyRecipeProvider {
  public SiliconAssemblyRecipe(PackOutput output) {
    super(output, BCRebornSilicon.MODID);
  }

  @Override
  public void create() {
    addRecipe(
      AssemblyRecipeBuilder.create(loc("redstone_chipset"))
        .addIngredient(Items.REDSTONE)
        .setEnergy(100000)
        .setResult(new ItemStack(SiliconItems.REDSTONE_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("iron_chipset"))
        .addIngredient(Items.REDSTONE, Items.IRON_INGOT)
        .setEnergy(200000)
        .setResult(new ItemStack(SiliconItems.IRON_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("gold_chipset"))
        .addIngredient(Items.REDSTONE, Items.GOLD_INGOT)
        .setEnergy(400000)
        .setResult(new ItemStack(SiliconItems.GOLD_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("diamond_chipset"))
        .addIngredient(Items.REDSTONE, Items.DIAMOND)
        .setEnergy(800000)
        .setResult(new ItemStack(SiliconItems.DIAMOND_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("emerald_chipset"))
        .addIngredient(Items.REDSTONE, Items.EMERALD)
        .setEnergy(1200000)
        .setResult(new ItemStack(SiliconItems.EMERALD_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("pulsating_chipset"))
        .addIngredient(Items.REDSTONE, Items.ENDER_PEARL)
        .setEnergy(400000)
        .setResult(new ItemStack(SiliconItems.PULSATING_CHIPSET.get(), 2))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("quartz_chipset"))
        .addIngredient(Items.REDSTONE, Items.QUARTZ)
        .setEnergy(600000)
        .setResult(new ItemStack(SiliconItems.QUARTZ_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("diamond_chipset"))
        .addIngredient(Items.REDSTONE, Items.COMPARATOR)
        .setEnergy(600000)
        .setResult(new ItemStack(SiliconItems.COMP_CHIPSET.get()))
    );

    addRecipe(
      AssemblyRecipeBuilder.create(loc("redstone_crystal"))
        .addIngredient(Items.REDSTONE_BLOCK)
        .setEnergy(10000000)
        .setResult(new ItemStack(SiliconItems.REDSTONE_CRYSTAL.get()))
    );
  }
}
