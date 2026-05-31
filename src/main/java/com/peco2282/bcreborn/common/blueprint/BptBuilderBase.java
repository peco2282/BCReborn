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
package com.peco2282.bcreborn.common.blueprint;


import com.peco2282.bcreborn.api.blueprints.BuilderAPI;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.MappingNotFoundException;
import com.peco2282.bcreborn.api.blueprints.SchematicBlockBase;
import com.peco2282.bcreborn.api.core.BCLog;
import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.IAreaProvider;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.builder.*;
import com.peco2282.bcreborn.common.utils.BCFakePlayer;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import com.peco2282.bcreborn.core.ItemsCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.level.BlockEvent;

import java.util.BitSet;

public abstract class BptBuilderBase implements IAreaProvider {

  public BlueprintBase blueprint;
  public BptContext context;
  protected BitSet usedLocations;
  protected boolean done;
  protected int x, y, z;
  protected boolean initialized = false;

  private long nextBuildDate = 0;

  public BptBuilderBase(BlueprintBase bluePrint, Level world, int x, int y, int z) {
    this.blueprint = bluePrint;
    this.x = x;
    this.y = y;
    this.z = z;
    this.usedLocations = new BitSet(bluePrint.sizeX * bluePrint.sizeY * bluePrint.sizeZ);
    done = false;

    Box box = new Box();
    box.initialize(this);

    context = bluePrint.getContext(world, box);
  }

  protected boolean isLocationUsed(int i, int j, int k) {
    int xCoord = i - x + blueprint.anchorX;
    int yCoord = j - y + blueprint.anchorY;
    int zCoord = k - z + blueprint.anchorZ;
    return usedLocations.get((zCoord * blueprint.sizeY + yCoord) * blueprint.sizeX + xCoord);
  }

  protected void markLocationUsed(int i, int j, int k) {
    int xCoord = i - x + blueprint.anchorX;
    int yCoord = j - y + blueprint.anchorY;
    int zCoord = k - z + blueprint.anchorZ;
    usedLocations.set((zCoord * blueprint.sizeY + yCoord) * blueprint.sizeX + xCoord, true);
  }

  public void initialize() {
    if (!initialized) {
      internalInit();
      initialized = true;
    }
  }

  protected abstract void internalInit();

  protected abstract BuildingSlot reserveNextBlock(Level world);

  protected abstract BuildingSlot getNextBlock(Level world, TileAbstractBuilder inv);

  public boolean buildNextSlot(Level world, TileAbstractBuilder builder, double x, double y, double z) {
    initialize();

    if (world.getGameTime() < nextBuildDate) {
      return false;
    }

    BuildingSlot slot = getNextBlock(world, builder);

    if (buildSlot(world, builder, slot, x + 0.5F, y + 0.5F, z + 0.5F)) {
      nextBuildDate = world.getGameTime() + slot.buildTime();
      return true;
    } else {
      return false;
    }
  }

  public boolean buildSlot(Level world, IBuildingItemsProvider builder, BuildingSlot slot, double x, double y,
                           double z) {
    initialize();

    if (slot != null) {
      slot.built = true;
      BuildingItem i = new BuildingItem();
      i.origin = new Position(x, y, z);
      i.destination = slot.getDestination();
      i.slotToBuild = slot;
      i.context = getContext();
      i.setStacksToDisplay(slot.getStacksToDisplay());
      builder.addAndLaunchBuildingItem(i);

      return true;
    }

    return false;
  }

  public BuildingSlot reserveNextSlot(Level world) {
    initialize();

    return reserveNextBlock(world);
  }

  @Override
  public int xMin() {
    return x - blueprint.anchorX;
  }

  @Override
  public int yMin() {
    return y - blueprint.anchorY;
  }

  @Override
  public int zMin() {
    return z - blueprint.anchorZ;
  }

  @Override
  public int xMax() {
    return x + blueprint.sizeX - blueprint.anchorX - 1;
  }

  @Override
  public int yMax() {
    return y + blueprint.sizeY - blueprint.anchorY - 1;
  }

  @Override
  public int zMax() {
    return z + blueprint.sizeZ - blueprint.anchorZ - 1;
  }

  @Override
  public void removeFromWorld() {

  }

  public AABB getBoundingBox() {
    return AABB.of(new BoundingBox(xMin(), yMin(), zMin(), xMax(), yMax(), zMax()));
  }

  public void postProcessing(Level world) {

  }

  public BptContext getContext() {
    return context;
  }

  public boolean isDone(IBuildingItemsProvider builder) {
    return done && builder.getBuilders().size() == 0;
  }

  private int getBlockBreakEnergy(BuildingSlotBlock slot) {
    return BlockUtils.computeBlockBreakEnergy(context.world(), new BlockPos(slot.x, slot.y, slot.z));
  }

  protected final boolean canDestroy(TileAbstractBuilder builder, IBuilderContext context, BuildingSlotBlock slot) {
    return builder.energyAvailable() >= getBlockBreakEnergy(slot);
  }

  public void consumeEnergyToDestroy(TileAbstractBuilder builder, BuildingSlotBlock slot) {
    builder.consumeEnergy(getBlockBreakEnergy(slot));
  }

  public void createDestroyItems(BuildingSlotBlock slot) {
    int hardness = (int) Math.ceil((double) getBlockBreakEnergy(slot) / BuilderAPI.BREAK_ENERGY);

    for (int i = 0; i < hardness; ++i) {
      slot.addStackConsumed(new ItemStack(ItemsCore.BUILD_TOOL_BOX.get()));
    }
  }

  public void useRequirements(Container inv, BuildingSlot slot) {

  }

  public void saveBuildStateToNBT(CompoundTag nbt, IBuildingItemsProvider builder) {
    nbt.putByteArray("usedLocationList", usedLocations.toByteArray());

    ListTag buildingList = new ListTag();

    for (BuildingItem item : builder.getBuilders()) {
      CompoundTag sub = new CompoundTag();
      item.writeToNBT(sub);
      buildingList.add(sub);
    }

    nbt.put("buildersInAction", buildingList);
  }

  public void loadBuildStateToNBT(CompoundTag nbt, IBuildingItemsProvider builder) {
    if (nbt.contains("usedLocationList")) {
      usedLocations = BitSet.valueOf(nbt.getByteArray("usedLocationList"));
    }

    ListTag buildingList = nbt
      .getList("buildersInAction",
        ListTag.TAG_COMPOUND);

    for (int i = 0; i < buildingList.size(); ++i) {
      BuildingItem item = new BuildingItem();

      try {
        item.readFromNBT(buildingList.getCompound(i));
        item.context = getContext();
        builder.getBuilders().add(item);
      } catch (MappingNotFoundException e) {
        BCLog.logger.log(org.apache.logging.log4j.Level.WARN, "can't load building item", e);
      }
    }

    // 6.4.6 and below migration

    if (nbt.contains("clearList")) {
      ListTag clearList = nbt.getList("clearList", ListTag.TAG_COMPOUND);

      for (int i = 0; i < clearList.size(); ++i) {
        CompoundTag cpt = clearList.getCompound(i);
        BlockIndex o = new BlockIndex(cpt);
        markLocationUsed(o.x, o.y, o.z);
      }
    }

    if (nbt.contains("builtList")) {
      ListTag builtList = nbt.getList("builtList", ListTag.TAG_COMPOUND);

      for (int i = 0; i < builtList.size(); ++i) {
        CompoundTag cpt = builtList.getCompound(i);
        BlockIndex o = new BlockIndex(cpt);
        markLocationUsed(o.x, o.y, o.z);
      }
    }
  }

  protected boolean isBlockBreakCanceled(Level world, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    if (!world.getBlockState(pos).isAir()) {
      BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, pos,
        world.getBlockState(pos),
        BCFakePlayer.createBuildCraftPlayer((ServerLevel) world, this.x, this.y, this.z));
      MinecraftForge.EVENT_BUS.post(breakEvent);
      return breakEvent.isCanceled();
    }
    return false;
  }

  protected boolean isBlockPlaceCanceled(Level world, int x, int y, int z, SchematicBlockBase schematic) {
    BlockPos pos = new BlockPos(x, y, z);
    BlockEvent.EntityPlaceEvent placeEvent = new BlockEvent.EntityPlaceEvent(
      BlockSnapshot.create(world.dimension(), world, pos),
      world.getBlockState(pos),
      BCFakePlayer.createBuildCraftPlayer((ServerLevel) world, this.x, this.y, this.z)
    );

    MinecraftForge.EVENT_BUS.post(placeEvent);
    return placeEvent.isCanceled();
  }
}
