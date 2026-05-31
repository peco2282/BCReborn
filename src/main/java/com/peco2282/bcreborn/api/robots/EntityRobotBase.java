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
package com.peco2282.bcreborn.api.robots;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.core.IZone;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class EntityRobotBase extends PathfinderMob implements Container, IFluidHandler{
    public static final int MAX_ENERGY = 100000;
    public static final int SAFETY_ENERGY = MAX_ENERGY / 5;
    public static final int SHUTDOWN_ENERGY = 0;
    public static final long NULL_ROBOT_ID = Long.MAX_VALUE;

    public EntityRobotBase(net.minecraft.world.entity.EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
    }

    public abstract void setItemInUse(ItemStack stack);

    public abstract void setItemActive(boolean b);

    public abstract boolean isMoving();

    public abstract DockingStation getLinkedStation();

    public abstract RedstoneBoardRobot getBoard();

    public abstract void aimItemAt(float yaw, float pitch);

    public abstract void aimItemAt(int x, int y, int z);

    public abstract float getAimYaw();

    public abstract float getAimPitch();

    public abstract int getEnergy();

    public abstract IEnergyStorage getBattery();

    public abstract DockingStation getDockingStation();

    public abstract void dock(DockingStation station);

    public abstract void undock();

    public abstract IZone getZoneToWork();

    public abstract IZone getZoneToLoadUnload();

    public abstract boolean containsItems();

    public abstract boolean hasFreeSlot();

    public abstract void unreachableEntityDetected(Entity entity);

    public abstract boolean isKnownUnreachable(Entity entity);

    public abstract long getRobotId();

    public abstract IRobotRegistry getRegistry();

    public abstract void releaseResources();

    public abstract void onChunkUnload();

    public abstract ItemStack receiveItem(BlockEntity tile, ItemStack stack);

    public abstract void setMainStation(DockingStation station);

    public abstract IFluidHandler getFluidHandler();
}
