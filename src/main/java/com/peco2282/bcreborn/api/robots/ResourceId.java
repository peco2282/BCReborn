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
public abstract class ResourceId {

  protected ResourceId() {
  }

  /**
   * Loads a ResourceId instance from NBT.
   *
   * @param nbt The NBT tag.
   * @return The loaded ResourceId, or {@code null} if loading failed.
   */
  public static ResourceId load(CompoundTag nbt) {
    try {
      Class<?> cls;
      if (nbt.contains("class")) {
        cls = RobotManager.getResourceIdByLegacyClassName(nbt.getString("class"));
      } else {
        cls = RobotManager.getResourceIdByName(nbt.getString("resourceName"));
      }

      ResourceId id = (ResourceId) cls.getDeclaredConstructor().newInstance();
      id.readFromNBT(nbt);

      return id;
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Writes the resource ID to NBT.
   *
   * @param nbt The NBT tag.
   */
  public void writeToNBT(CompoundTag nbt) {
    nbt.putString("resourceName", RobotManager.getResourceIdName(getClass()));
  }

  /**
   * Reads the resource ID from NBT.
   *
   * @param nbt The NBT tag.
   */
  protected void readFromNBT(CompoundTag nbt) {
  }
}
