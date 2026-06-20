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
package com.peco2282.bcreborn.builders.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.common.builder.schematics.SchematicBlockFloored;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.LinkedList;

public class SchematicDoor extends SchematicBlockFloored {

  final ItemStack stack;

  public SchematicDoor(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
      requirements.add(stack.copy());
    }
  }

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {
    // cancel requirements reading
  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
  }

  @Override
  public boolean doNotBuild() {
    return state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
  }

  @Override
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    BlockState worldState = context.world().getBlockState(new BlockPos(x, y, z));
    return state.getBlock() == worldState.getBlock() && state.getValue(DoorBlock.HALF) == worldState.getValue(DoorBlock.HALF);
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    context.world().setBlock(new BlockPos(x, y, z), state, 3);
  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    super.initializeFromObjectAt(context, x, y, z);
  }

  @Override
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
    super.writeSchematicToNBT(nbt, registry);
  }

  @Override
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
    super.readSchematicFromNBT(nbt, registry);
  }
}
