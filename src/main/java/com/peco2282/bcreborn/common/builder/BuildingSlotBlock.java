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

public class BuildingSlotBlock extends BuildingSlot {
    public int x, y, z;

    public enum Mode {
        ClearIfInvalid, Build
    }

    public Mode mode = Mode.Build;
    public int buildStage = 0;

    @Override
    public Schematic getSchematic() {
        return null;
    }

    @Override
    public Position getDestination() {
        Position p = new Position();
        p.x = x;
        p.y = y;
        p.z = z;
        return p;
    }

    @Override
    public boolean writeToWorld(IBuilderContext context) {
        return false;
    }

    @Override
    public LinkedList<ItemStack> getRequirements(IBuilderContext context) {
        return new LinkedList<>();
    }

    @Override
    public boolean isAlreadyBuilt(IBuilderContext context) {
        return false;
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
        return 0;
    }

    @Override
    public int buildTime() {
        return 1;
    }
}
