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

import com.peco2282.bcreborn.api.blueprints.BuildingPermission;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.common.block.EngineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;

/**
 * Original {@code SchematicEngine}: an engine is built with its orientation and nothing else. The original copied
 * the tile NBT and removed the running state (progress, energy, heat, fuel and coolant tanks); here the orientation is
 * the block state's {@code FACING}, which the placed engine reads, so no block entity data is kept at all. The engine
 * is placed idle and costs one engine item.
 * <p>
 * Without this schematic the engines fell to the generic block entity schematic, which only a creative builder may
 * place. The creative engine stays creative only.
 */
public class SchematicEngine extends SchematicBlock {
  private final boolean creativeOnly;

  public SchematicEngine() {
    this(false);
  }

  public SchematicEngine(boolean creativeOnly) {
    this.creativeOnly = creativeOnly;
  }

  @Override
  public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    super.initializeFromObjectAt(context, x, y, z);
    state = idle(state);
  }

  private static BlockState idle(BlockState state) {
    return state.hasProperty(EngineBlock.ACTIVE) ? state.setValue(EngineBlock.ACTIVE, false) : state;
  }

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    requirements.add(new ItemStack(state.getBlock()));
  }

  /** A running engine still counts as built: only the engine and its orientation matter. */
  @Override
  public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
    BlockState world = context.world().getBlockState(new BlockPos(x, y, z));
    return world.is(state.getBlock()) && idle(world) == idle(state);
  }

  @Override
  public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    context.world().setBlock(new BlockPos(x, y, z), idle(state), 3);
  }

  @Override
  public BuildingPermission getBuildingPermission() {
    return creativeOnly ? BuildingPermission.CREATIVE_ONLY : super.getBuildingPermission();
  }
}
