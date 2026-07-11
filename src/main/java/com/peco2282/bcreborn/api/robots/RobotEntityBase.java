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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all robot entities.
 * <p>
 * This class extends {@link PathfinderMob} and implements {@link Container} and {@link IFluidHandler},
 * providing a foundation for robots that can navigate the world, carry items, and handle fluids.
 */
public abstract class RobotEntityBase extends PathfinderMob implements Container, IFluidHandler {
  /**
   * The maximum energy capacity of the robot.
   */
  public static final int MAX_ENERGY = 100000;

  /**
   * The energy level below which the robot should seek a docking station for recharging.
   */
  public static final int SAFETY_ENERGY = MAX_ENERGY / 5;

  /**
   * The energy level at which the robot shuts down.
   */
  public static final int SHUTDOWN_ENERGY = 0;

  /**
   * Constant representing a null robot ID.
   */
  public static final long NULL_ROBOT_ID = Long.MAX_VALUE;

  /**
   * Constructs a new RobotEntityBase.
   *
   * @param type  The entity type.
   * @param world The world the robot is in.
   */
  public RobotEntityBase(EntityType<? extends PathfinderMob> type, Level world) {
    super(type, world);
  }

  /**
   * Sets the item currently in use by the robot.
   *
   * @param stack The item stack.
   */
  public abstract void setItemInUse(ItemStack stack);

  /**
   * Sets whether the robot's current item is active.
   *
   * @param b {@code true} to activate, {@code false} to deactivate.
   */
  public abstract void setItemActive(boolean b);

  /**
   * Checks if the robot is currently moving.
   *
   * @return {@code true} if moving, {@code false} otherwise.
   */
  public abstract boolean isMoving();

  /**
   * Returns the docking station this robot is linked to.
   *
   * @return The linked docking station.
   */
  @Nullable
  public abstract DockingStation<?> getLinkedStation();

  /**
   * Returns the redstone board installed in this robot.
   *
   * @return The redstone board.
   */
  @Nullable
  public abstract RedstoneBoardRobot<?> getBoard();

  /**
   * Aims the robot's current item at the specified yaw and pitch.
   *
   * @param yaw   The yaw angle.
   * @param pitch The pitch angle.
   */
  public abstract void aimItemAt(float yaw, float pitch);

  /**
   * Aims the robot's current item at the specified block coordinates.
   *
   * @param x The X coordinate.
   * @param y The Y coordinate.
   * @param z The Z coordinate.
   */
  public abstract void aimItemAt(int x, int y, int z);

  /**
   * Returns the current aiming yaw.
   *
   * @return The yaw.
   */
  public abstract float getAimYaw();

  /**
   * Returns the current aiming pitch.
   *
   * @return The pitch.
   */
  public abstract float getAimPitch();

  /**
   * Returns the current energy level of the robot.
   *
   * @return The energy level.
   */
  public abstract int getEnergy();

  /**
   * Returns the energy storage (battery) of the robot.
   *
   * @return The energy storage.
   */
  public abstract IEnergyStorage getBattery();

  /**
   * Returns the docking station the robot is currently docked at.
   *
   * @return The docking station, or {@code null} if not docked.
   */
  @Nullable
  public abstract DockingStation<?> getDockingStation();

  /**
   * Docks the robot at the specified station.
   *
   * @param station The docking station.
   */
  public abstract void dock(DockingStation<?> station);

  /**
   * Undocks the robot from its current station.
   */
  public abstract void undock();

  /**
   * Returns the zone where the robot is assigned to work.
   *
   * @return The work zone.
   */
  @Nullable
  public abstract IZone getZoneToWork();

  /**
   * Returns the zone where the robot is assigned to load or unload items/fluids.
   *
   * @return The load/unload zone.
   */
  @Nullable
  public abstract IZone getZoneToLoadUnload();

  /**
   * Checks if the robot contains any items.
   *
   * @return {@code true} if it contains items, {@code false} otherwise.
   */
  public abstract boolean containsItems();

  /**
   * Checks if the robot has at least one free inventory slot.
   *
   * @return {@code true} if there's a free slot, {@code false} otherwise.
   */
  public abstract boolean hasFreeSlot();

  /**
   * Notifies the robot that an entity is unreachable.
   *
   * @param entity The unreachable entity.
   */
  public abstract void unreachableEntityDetected(Entity entity);

  /**
   * Checks if an entity is known to be unreachable.
   *
   * @param entity The entity to check.
   * @return {@code true} if unreachable, {@code false} otherwise.
   */
  public abstract boolean isKnownUnreachable(Entity entity);

  /**
   * Returns the unique ID of this robot.
   *
   * @return The robot ID.
   */
  public abstract long getRobotId();

  /**
   * Returns the robot registry for the world this robot is in.
   *
   * @return The robot registry.
   */
  public abstract IRobotRegistry getRegistry();

  /**
   * Releases resources associated with this robot.
   */
  public abstract void releaseResources();

  /**
   * Called when the chunk containing this robot is unloaded.
   */
  public abstract void onChunkUnload();

  /**
   * Receives an item from a block entity.
   *
   * @param tile  The block entity providing the item.
   * @param stack The item stack being provided.
   * @return The remaining item stack after the robot has taken what it can.
   */
  public abstract ItemStack receiveItem(BlockEntity tile, ItemStack stack);

  /**
   * Sets the main station for this robot.
   *
   * @param station The main docking station.
   */
  public abstract void setMainStation(DockingStation<?> station);

  /**
   * Returns the fluid handler for this robot.
   *
   * @return The fluid handler.
   */
  public abstract IFluidHandler getFluidHandler();

  /**
   * Saves robot-specific data to the given {@link CompoundTag}.
   * Subclasses should override this to persist their state,
   * calling {@code super.addAdditionalSaveData(nbt)} first.
   *
   * @param nbt The tag to write data into.
   */
  @Override
  public void addAdditionalSaveData(CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
  }

  /**
   * Loads robot-specific data from the given {@link CompoundTag}.
   * Subclasses should override this to restore their state,
   * calling {@code super.readAdditionalSaveData(nbt)} first.
   *
   * @param nbt The tag to read data from.
   */
  @Override
  public void readAdditionalSaveData(CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
  }

  public abstract MutableComponent getDisplayName();
}
