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
package com.peco2282.bcreborn.builders.quarry;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MecanicalArmEntity extends Entity {
  private BlockPos pos;
  private Vec3 headPos;
  private double armSizeX;
  private double armSizeZ;

  public MecanicalArmEntity(EntityType<?> p_19870_, Level p_19871_) {
    super(p_19870_, p_19871_);
  }

  @Override
  protected void defineSynchedData() {

  }

  @Override
  protected void readAdditionalSaveData(CompoundTag p_20052_) {

  }

  @Override
  protected void addAdditionalSaveData(CompoundTag p_20139_) {

  }
}
