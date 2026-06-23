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

import net.minecraft.nbt.CompoundTag;

/**
 * Base class for resource identifiers used by robots.
 */
public abstract class ResourceId<T extends ResourceId<T>> {

  protected ResourceIdType<T> type;

  protected ResourceId(ResourceIdType<T> type) {
    this.type = type;
  }

  /**
   * Loads a ResourceId instance from NBT.
   *
   * @param nbt The NBT tag.
   * @return The loaded ResourceId, or {@code null} if loading failed.
   */
  public static <T extends ResourceId<T>> T load(CompoundTag nbt) {
    return RobotManager.createResourceId(nbt.getString("resourceName"), nbt);
  }

  /**
   * Writes the resource ID to NBT.
   *
   * @param nbt The NBT tag.
   */
  public void writeToNBT(CompoundTag nbt) {
    nbt.putString("resourceName", type.id().toString());
  }

  /**
   * Reads the resource ID from NBT.
   *
   * @param nbt The NBT tag.
   */
  protected void readFromNBT(CompoundTag nbt) {
  }

  public ResourceIdType<T> getType() {
    return type;
  }
}
