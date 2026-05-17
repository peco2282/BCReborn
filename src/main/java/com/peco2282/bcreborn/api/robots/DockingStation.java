/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.api.robots;

import com.peco2282.bcreborn.api.statements.StatementSlot;
import com.peco2282.bcreborn.api.transport.IInjectable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class DockingStation {
    public Direction side;
    public Level world;

    private static final long NULL_ROBOT_ID = -1;
    private long robotTakingId = NULL_ROBOT_ID;
    private Object robotTaking;
    private boolean linkIsMain = false;
    private BlockPos index;

    public DockingStation(BlockPos iIndex, Direction iSide) {
        index = iIndex;
        side = iSide;
    }

    public DockingStation() {
    }

    public boolean isMainStation() {
        return linkIsMain;
    }

    public int x() {
        return index.getX();
    }

    public int y() {
        return index.getY();
    }

    public int z() {
        return index.getZ();
    }

    public Direction side() {
        return side;
    }

    public Object robotTaking() {
        return robotTaking;
    }

    public void invalidateRobotTakingEntity() {
        robotTaking = null;
    }

    public long linkedId() {
        return robotTakingId;
    }

    public boolean isTaken() {
        return robotTakingId != NULL_ROBOT_ID;
    }

    public long robotIdTaking() {
        return robotTakingId;
    }

    public BlockPos index() {
        return index;
    }

    public void writeToNBT(CompoundTag nbt) {
        CompoundTag indexNBT = new CompoundTag();
        indexNBT.putInt("x", index.getX());
        indexNBT.putInt("y", index.getY());
        indexNBT.putInt("z", index.getZ());
        nbt.put("index", indexNBT);
        nbt.putByte("side", (byte) (side != null ? side.ordinal() : 0));
        nbt.putBoolean("isMain", linkIsMain);
        nbt.putLong("robotId", robotTakingId);
    }

    public void readFromNBT(CompoundTag nbt) {
        CompoundTag indexNBT = nbt.getCompound("index");
        index = new BlockPos(indexNBT.getInt("x"), indexNBT.getInt("y"), indexNBT.getInt("z"));
        side = Direction.values()[nbt.getByte("side")];
        linkIsMain = nbt.getBoolean("isMain");
        robotTakingId = nbt.getLong("robotId");
    }

    public boolean linkIsDocked() {
        return false;
    }

    public boolean canRelease() {
        return !isMainStation() && !linkIsDocked();
    }

    public boolean isInitialized() {
        return true;
    }

    public abstract Iterable<StatementSlot> getActiveActions();

    public IInjectable getItemOutput() {
        return null;
    }

    public Direction getItemOutputSide() {
        return null;
    }

    public Container getItemInput() {
        return null;
    }

    public Direction getItemInputSide() {
        return null;
    }

    public IFluidHandler getFluidOutput() {
        return null;
    }

    public Direction getFluidOutputSide() {
        return null;
    }

    public IFluidHandler getFluidInput() {
        return null;
    }

    public Direction getFluidInputSide() {
        return null;
    }

    public boolean providesPower() {
        return false;
    }

    public IRequestProvider getRequestProvider() {
        return null;
    }

    public void onChunkUnload() {
    }

    @Override
    public String toString() {
        return "{" + index.getX() + ", " + index.getY() + ", " + index.getZ() + ", " + side + " :" + robotTakingId + "}";
    }
}
