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
package com.peco2282.bcreborn.api.blueprints;

import com.peco2282.bcreborn.api.core.Position;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Schematic implementation for entities.
 */
public class SchematicEntity extends Schematic {

  /**
   * The type of the entity.
   */
  public EntityType<? extends Entity> entity;

  /**
   * The NBT data of the entity.
   */
  public CompoundTag entityNBT = new CompoundTag();

  /**
   * Requirements for the entity when stored in a blueprint.
   */
  public ItemStack[] storedRequirements = new ItemStack[0];

  /**
   * The default building permission for this entity.
   */
  public BuildingPermission defaultPermission = BuildingPermission.ALL;

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    Collections.addAll(requirements, storedRequirements);
  }

  /**
   * Spawns the entity in the world based on the stored NBT data.
   *
   * @param context The builder context.
   */
  public void writeToWorld(IBuilderContext context) {
    Optional<Entity> e = EntityType.create(entityNBT, context.world());
    e.ifPresent(context.world()::addFreshEntity);
  }

  /**
   * Initializes the schematic from an existing entity in the world.
   *
   * @param context The builder context.
   * @param entity  The entity to read from.
   */
  public void readFromWorld(IBuilderContext context, Entity entity) {
    entity.save(entityNBT);
  }

  @Override
  public void translateToBlueprint(Translation transform) {
    ListTag nbttaglist = entityNBT.getList("Pos", Tag.TAG_DOUBLE);
    Position pos = new Position(nbttaglist.getDouble(0),
      nbttaglist.getDouble(1), nbttaglist.getDouble(2));
    pos = transform.translate(pos);

    entityNBT.put("Pos", newDoubleNBTList(pos.x, pos.y, pos.z));
  }

  @Override
  public void translateToWorld(Translation transform) {
    ListTag nbttaglist = entityNBT.getList("Pos", Tag.TAG_DOUBLE);
    Position pos = new Position(nbttaglist.getDouble(0),
      nbttaglist.getDouble(1), nbttaglist.getDouble(2));
    pos = transform.translate(pos);

    entityNBT.put("Pos", newDoubleNBTList(pos.x, pos.y, pos.z));
  }

  @Override
  public void idsToBlueprint(MappingRegistry registry) {
    registry.scanAndTranslateStacksToRegistry(entityNBT);
  }

  @Override
  public void idsToWorld(MappingRegistry registry) {
    try {
      registry.scanAndTranslateStacksToWorld(entityNBT);
    } catch (MappingNotFoundException e) {
      entityNBT = new CompoundTag();
    }
  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    ListTag nbttaglist = entityNBT.getList("Pos", Tag.TAG_DOUBLE);
    Position pos = new Position(nbttaglist.getDouble(0),
      nbttaglist.getDouble(1), nbttaglist.getDouble(2));
    pos = context.rotatePositionLeft(pos);
    entityNBT.put("Pos", newDoubleNBTList(pos.x, pos.y, pos.z));

    ListTag rotList = entityNBT.getList("Rotation", Tag.TAG_FLOAT);
    float yaw = rotList.getFloat(0);
    yaw += 90;
    entityNBT.put("Rotation", newFloatNBTList(yaw, rotList.getFloat(1)));
  }

  @Override
  public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
    super.writeSchematicToNBT(nbt, registry);

    nbt.putInt("entityId", registry.getIdForEntity(entity));
    nbt.put("entity", entityNBT);

    ListTag rq = new ListTag();

    for (ItemStack stack : storedRequirements) {
      CompoundTag sub = new CompoundTag();
      stack.save(sub);
      sub.putInt("id", registry.getIdForItem(stack.getItem()));
      rq.add(sub);
    }

    nbt.put("rq", rq);
  }

  @Override
  public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
    super.readSchematicFromNBT(nbt, registry);

    entityNBT = nbt.getCompound("entity");

    ListTag rq = nbt.getList("rq", Tag.TAG_COMPOUND);

    ArrayList<ItemStack> rqs = new ArrayList<>();

    for (int i = 0; i < rq.size(); ++i) {
      try {
        CompoundTag sub = rq.getCompound(i);

        if (sub.getInt("id") >= 0) {
          ItemStack stack = ItemStack.of(sub);
          if (!stack.isEmpty()) {
            rqs.add(stack);
          }
        } else {
          defaultPermission = BuildingPermission.CREATIVE_ONLY;
        }
      } catch (Throwable t) {
        t.printStackTrace();
        defaultPermission = BuildingPermission.CREATIVE_ONLY;
      }
    }

    storedRequirements = rqs.toArray(new ItemStack[0]);
  }

  /**
   * Creates an NBT list of doubles.
   *
   * @param values The double values.
   * @return The {@link ListTag}.
   */
  protected ListTag newDoubleNBTList(double... values) {
    ListTag nbttaglist = new ListTag();
    for (double d : values) {
      nbttaglist.add(DoubleTag.valueOf(d));
    }
    return nbttaglist;
  }

  /**
   * Creates an NBT list of floats.
   *
   * @param values The float values.
   * @return The {@link ListTag}.
   */
  protected ListTag newFloatNBTList(float... values) {
    ListTag nbttaglist = new ListTag();
    for (float f : values) {
      nbttaglist.add(FloatTag.valueOf(f));
    }
    return nbttaglist;
  }

  /**
   * Checks if the entity is already present in the world near its intended position.
   *
   * @param context The builder context.
   * @return True if the entity exists.
   */
  public boolean isAlreadyBuilt(IBuilderContext context) {
    ListTag nbttaglist = entityNBT.getList("Pos", Tag.TAG_DOUBLE);
    Position newPosition = new Position(nbttaglist.getDouble(0),
      nbttaglist.getDouble(1), nbttaglist.getDouble(2));

    for (Entity e : context.world().getEntitiesOfClass(Entity.class,
      new AABB(newPosition.x - 1, newPosition.y - 1, newPosition.z - 1,
        newPosition.x + 1, newPosition.y + 1, newPosition.z + 1))) {
      Position existingPosition = new Position(e.getX(), e.getY(), e.getZ());

      if (existingPosition.isClose(newPosition, 0.1F)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public int buildTime() {
    return 5;
  }

  @Override
  public BuildingPermission getBuildingPermission() {
    return defaultPermission;
  }
}
