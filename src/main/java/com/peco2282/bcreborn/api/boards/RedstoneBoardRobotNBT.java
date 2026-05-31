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
package com.peco2282.bcreborn.api.boards;

import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public abstract class RedstoneBoardRobotNBT extends RedstoneBoardNBT<EntityRobotBase> {

  @Override
  public RedstoneBoardRobot create(CompoundTag nbt, EntityRobotBase robot) {
    return create(robot);
  }

  public abstract RedstoneBoardRobot create(Object robot);

  public abstract ResourceLocation getRobotTexture();
}
