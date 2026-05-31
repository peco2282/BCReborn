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
package com.peco2282.bcreborn.silicon.entity;

import com.peco2282.bcreborn.silicon.SiliconEntityTypes;
import com.peco2282.bcreborn.silicon.SiliconItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class PackageEntity extends ThrowableItemProjectile {
  private ItemStack pkg;

  public PackageEntity(EntityType<? extends PackageEntity> type, Level world) {
    super(type, world);
    this.pkg = new ItemStack(SiliconItems.PACKAGE_ITEM.get());
  }

  public PackageEntity(Level world, LivingEntity player, ItemStack stack) {
    super(SiliconEntityTypes.PACKAGE.get(), player, world);
    this.pkg = stack;
  }

  public PackageEntity(Level world, double x, double y, double z, ItemStack stack) {
    super(SiliconEntityTypes.PACKAGE.get(), x, y, z, world);
    this.pkg = stack;
  }

  @Override
  protected Item getDefaultItem() {
    return SiliconItems.PACKAGE_ITEM.get();
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compound) {
    super.addAdditionalSaveData(compound);
    if (pkg != null) {
      compound.put("stack", pkg.save(new CompoundTag()));
    }
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compound) {
    super.readAdditionalSaveData(compound);
    if (compound.contains("stack")) {
      pkg = ItemStack.of(compound.getCompound("stack"));
    }
  }

  @Override
  protected void onHit(HitResult target) {
    double x = target.getLocation().x;
    double y = target.getLocation().y;
    double z = target.getLocation().z;

    float hitPoints = 0.0F;
    // PackageItem.getStack needs to be accessible
    // For simplicity, we assume PackageItem has 9 slots
    for (int i = 0; i < 9; i++) {
      CompoundTag tag = pkg.getTag();
      ItemStack stack = ItemStack.EMPTY;
      if (tag != null && tag.contains("item" + i)) {
        stack = ItemStack.of(tag.getCompound("item" + i));
      }

      if (!stack.isEmpty()) {
        if (stack.getItem() instanceof BlockItem) {
          hitPoints += 0.28F;
        } else {
          hitPoints += 0.14F;
        }
        float var = 0.7F;
        double dx = level().random.nextFloat() * var + (1.0F - var) * 0.5D;
        double dy = level().random.nextFloat() * var + (1.0F - var) * 0.5D;
        double dz = level().random.nextFloat() * var + (1.0F - var) * 0.5D;
        ItemEntity entityitem = new ItemEntity(level(), x + dx, y + dy, z + dz, stack);
        entityitem.setPickUpDelay(10);

        level().addFreshEntity(entityitem);
      }
    }

    if (target.getType() == HitResult.Type.ENTITY) {
      ((EntityHitResult) target).getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), hitPoints);
    }

    discard();
  }
}
