/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.builder;

import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;

public class BuildingSlotEntity extends BuildingSlot {

    public int sequenceNumber;

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
