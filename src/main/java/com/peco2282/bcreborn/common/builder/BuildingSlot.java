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


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.MappingNotFoundException;
import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

public abstract class BuildingSlot {

  public LinkedList<ItemStack> stackConsumed;

  public boolean reserved = false;

  public boolean built = false;

  public boolean writeToWorld(IBuilderContext context) {
    return false;
  }

  public void writeCompleted(IBuilderContext context, double complete) {

  }

  public void postProcessing(IBuilderContext context) {

  }

  public LinkedList<ItemStack> getRequirements(IBuilderContext context) {
    return new LinkedList<ItemStack>();
  }

  public abstract Position getDestination();

  public void addStackConsumed(ItemStack stack) {
    if (stackConsumed == null) {
      stackConsumed = new LinkedList<>();
    }

    stackConsumed.add(stack);
  }

  public List<ItemStack> getStacksToDisplay() {
    return getSchematic().getStacksToDisplay(stackConsumed);
  }

  public abstract boolean isAlreadyBuilt(IBuilderContext context);

  public abstract Schematic getSchematic();

  public abstract void writeToNBT(CompoundTag nbt, MappingRegistry registry);

  public abstract void readFromNBT(CompoundTag nbt, MappingRegistry registry) throws MappingNotFoundException;

  public abstract int getEnergyRequirement();

  public abstract int buildTime();

}
