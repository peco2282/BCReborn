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
package com.peco2282.bcreborn.common.builder;

import com.peco2282.bcreborn.api.blueprints.*;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.common.blueprint.IndexRequirementMap;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedList;

public class BuildingSlotBlock extends BuildingSlot {
  public int x, y, z;
  public SchematicBlockBase schematic;
  public Mode mode = Mode.Build;
  public int buildStage = 0;
  /** Original {@code internalRequirementRemovalListener}: unblocks the slots that wait for this one. */
  @Nullable
  public IndexRequirementMap internalRequirementRemovalListener;

  @Override
  public SchematicBlockBase getSchematic() {
    if (schematic == null) {
      return new SchematicMask(false);
    }
    return schematic;
  }

  @Override
  public Position getDestination() {
    // Original: the building item flies to the block centre.
    return new Position(x + 0.5, y + 0.5, z + 0.5);
  }

  /**
   * Original {@code BuildingSlotBlock.writeToWorld()}.
   * <ul>
   *   <li>Clear: an invalid block is set to air (the template builder already collected its drops).</li>
   *   <li>Build: places the schematic with the consumed stacks. This branch was missing, so builders and fillers
   *       consumed items without placing anything. A failure while placing sets the position back to air
   *       instead of leaving a half-written block.</li>
   * </ul>
   */
  @Override
  public boolean writeToWorld(IBuilderContext context) {
    if (internalRequirementRemovalListener != null) {
      internalRequirementRemovalListener.remove(this);
    }
    BlockPos pos = new BlockPos(x, y, z);
    if (mode == Mode.ClearIfInvalid) {
      if (!getSchematic().isAlreadyBuilt(context, x, y, z)) {
        context.world().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        return true;
      }
      return false;
    }

    try {
      getSchematic().placeInWorld(context, x, y, z, stackConsumed == null ? new LinkedList<>() : stackConsumed);
      return true;
    } catch (Throwable t) {
      BCLog.logger.error("Failed to place a building slot at {}; setting it to air", pos, t);
      context.world().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
      return false;
    }
  }

  /** Original {@code postProcessing()}. */
  @Override
  public void postProcessing(IBuilderContext context) {
    getSchematic().postProcessing(context, x, y, z);
  }

  /** Original {@code getRequirements()}: clearing needs nothing, building needs the schematic's items. */
  @Override
  public LinkedList<ItemStack> getRequirements(IBuilderContext context) {
    LinkedList<ItemStack> req = new LinkedList<>();
    if (mode == Mode.Build) {
      getSchematic().getRequirementsForPlacement(context, req);
    }
    return req;
  }

  /** Original {@code isAlreadyBuilt()}: the schematic compares itself with the world. */
  @Override
  public boolean isAlreadyBuilt(IBuilderContext context) {
    return getSchematic().isAlreadyBuilt(context, x, y, z);
  }

  @Override
  public void writeToNBT(CompoundTag nbt, MappingRegistry registry) {
    nbt.putInt("x", x);
    nbt.putInt("y", y);
    nbt.putInt("z", z);
    nbt.putByte("mode", (byte) mode.ordinal());
    nbt.putInt("buildStage", buildStage);
  }

  @Override
  public void readFromNBT(CompoundTag nbt, MappingRegistry registry) throws MappingNotFoundException {
    x = nbt.getInt("x");
    y = nbt.getInt("y");
    z = nbt.getInt("z");
    mode = Mode.values()[nbt.getByte("mode")];
    buildStage = nbt.getInt("buildStage");
  }

  @Override
  public int getEnergyRequirement() {
    return getSchematic().getEnergyRequirement(stackConsumed == null ? new LinkedList<>() : stackConsumed);
  }

  @Override
  public int buildTime() {
    return schematic == null ? 1 : schematic.buildTime();
  }

  public enum Mode {
    ClearIfInvalid, Build
  }
}
