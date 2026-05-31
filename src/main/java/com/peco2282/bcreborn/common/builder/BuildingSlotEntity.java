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

public class BuildingSlotEntity extends BuildingSlot {

    public int sequenceNumber;public Schematic schematic;

    @Override
    public boolean writeToWorld(IBuilderContext context) {
        return false;
    }

    @Override
    public Position getDestination() {
        return new Position();
    }

    @Override
    public LinkedList<ItemStack> getRequirements(IBuilderContext context) {
        return new LinkedList<>();
    }

    @Override
    public Schematic getSchematic() {
        return null;
    }

    @Override
    public boolean isAlreadyBuilt(IBuilderContext context) {
        return false;
    }

    @Override
    public void writeToNBT(CompoundTag nbt, MappingRegistry registry) {
    }

    @Override
    public void readFromNBT(CompoundTag nbt, MappingRegistry registry) throws MappingNotFoundException {
    }

    @Override
    public int getEnergyRequirement() {
        return 0;
    }

    @Override
    public int buildTime() {
        return 1;
    }
}
