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
package com.peco2282.bcreborn.api.blueprints;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FluidState;

import java.util.LinkedList;

public class SchematicFluid extends SchematicBlock {

  private final ItemStack fluidItem;

  public SchematicFluid(Block fluidBlock) {
    this.state = fluidBlock.defaultBlockState();
    this.fluidItem = new ItemStack(fluidBlock);
  }

  public SchematicFluid(FluidState state) {
    this.state = state.createLegacyBlock();
    this.fluidItem = new ItemStack(this.state.getBlock());
  }

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    if (state != null && state.hasProperty(LiquidBlock.LEVEL) && state.getValue(LiquidBlock.LEVEL) == 0) {
      requirements.add(fluidItem.copy());
    }
  }

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {
    // cancel requirements reading
  }

  @Override
  public void rotateLeft(IBuilderContext context) {

  }

  @Override
  public boolean doNotBuild() {
    if (state != null && state.hasProperty(LiquidBlock.LEVEL)) {
      return state.getValue(LiquidBlock.LEVEL) != 0;
    }
    return false;
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    if (!doNotBuild()) {
      context.world().setBlock(new BlockPos(x, y, z), state, 3);
    }
  }

  @Override
  public void postProcessing(IBuilderContext context, int x, int y, int z) {
    if (doNotBuild()) {
      context.world().setBlock(new BlockPos(x, y, z), state, 3);
    }
  }

  @Override
  public LinkedList<ItemStack> getStacksToDisplay(LinkedList<ItemStack> stackConsumed) {
    LinkedList<ItemStack> result = new LinkedList<>();
    result.add(fluidItem.copy());
    return result;
  }

  @Override
  public int getEnergyRequirement(LinkedList<ItemStack> stacksUsed) {
    return BuilderAPI.BUILD_ENERGY;
  }
}
