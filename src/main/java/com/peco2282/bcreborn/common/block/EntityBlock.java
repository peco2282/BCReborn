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
package com.peco2282.bcreborn.common.block;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class EntityBlock extends Entity {
  public float shadowSize = 0;
  public float rotationX = 0;
  public float rotationY = 0;
  public float rotationZ = 0;
  public double iSize, jSize, kSize;

  public EntityBlock(EntityType<?> type, Level world) {
    super(type, world);
    noPhysics = true;
  }

  public EntityBlock(EntityType<?> type, Level world, double xPos, double yPos, double zPos) {
    this(type, world);
    setPos(xPos, yPos, zPos);
  }

  public EntityBlock(EntityType<?> type, Level world, double i, double j, double k, double iSize, double jSize, double kSize) {
    this(type, world);
    this.iSize = iSize;
    this.jSize = jSize;
    this.kSize = kSize;
    setPos(i, j, k);
    setDeltaMovement(0, 0, 0);
  }

  @Override
  public void setPos(double d, double d1, double d2) {
    super.setPos(d, d1, d2);
    setBoundingBox(new net.minecraft.world.phys.AABB(getX(), getY(), getZ(), getX() + iSize, getY() + jSize, getZ() + kSize));
  }

  public void moveEntity(double d, double d1, double d2) {
    setPos(getX() + d, getY() + d1, getZ() + d2);
  }

  @Override
  protected void defineSynchedData() {

  }

  @Override
  protected void readAdditionalSaveData(CompoundTag data) {
    iSize = data.getDouble("iSize");
    jSize = data.getDouble("jSize");
    kSize = data.getDouble("kSize");
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag data) {
    data.putDouble("iSize", iSize);
    data.putDouble("jSize", jSize);
    data.putDouble("kSize", kSize);
  }

  @Override
  public Packet<ClientGamePacketListener> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
