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
package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.ConstructionMarkerBlock;
import com.peco2282.bcreborn.builders.item.BlueprintItem;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.LaserData;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.*;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.builder.IBuildingItemsProvider;
import com.peco2282.bcreborn.common.internal.IBoxProvider;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstructionMarkerBlockEntity extends BuildCraftBlockEntity implements IBuildingItemsProvider, IBoxProvider {
  public static Set<ConstructionMarkerBlockEntity> currentMarkers = new HashSet<>();
  public LaserData laser;
  public ItemStack blueprint = ItemStack.EMPTY;
  public Box box = new Box();
  public BptBuilderBase bluePrintBuilder;
  public BptContext bptContext;
  private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
  private final ArrayList<BuildingItem> buildersInAction = new ArrayList<>();
  private CompoundTag initNBT;

  public ConstructionMarkerBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityTypesBuilders.CONSTRUCTION_MARKER.get(), pos, state);
  }

  public Direction getDirection() {
    BlockState state = getBlockState();
    if (state.hasProperty(ConstructionMarkerBlock.FACING)) {
      return state.getValue(ConstructionMarkerBlock.FACING);
    }
    return Direction.NORTH;
  }

  public ItemStack getBlueprint() {
    return items.get(0);
  }

  public void setBlueprint(ItemStack stack) {
    items.set(0, stack);
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
  }

  public boolean hasBlueprint() {
    return !items.get(0).isEmpty();
  }

  public ItemStack removeBlueprint() {
    ItemStack stack = items.get(0);
    items.set(0, ItemStack.EMPTY);
    setChanged();
    if (level != null && !level.isClientSide) {
      level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
    return stack;
  }

  @Override
  public void initialize() {
    super.initialize();
    box.kind = Box.Kind.BLUE_STRIPES;

    if (level.isClientSide) {
      BCNetworkManager.sendUploadBuildersInAction(getBlockPos());
    }
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    BuildingItem toRemove = null;

    for (BuildingItem i : buildersInAction) {
      i.update();

      if (i.isDone) {
        toRemove = i;
      }
    }

    if (toRemove != null) {
      buildersInAction.remove(toRemove);
    }

    if (level.isClientSide) {
      return;
    }

    ItemStack itemBlueprint = getBlueprint();
    if (!itemBlueprint.isEmpty() && BlueprintItem.getId(itemBlueprint) != null && bluePrintBuilder == null) {
      BlueprintBase bpt = BlueprintItem.loadBlueprint(itemBlueprint);
      if (bpt instanceof Blueprint) {
        BlockPos pos1 = getBlockPos();
        bpt = bpt.adjustToWorld(level, pos1.getX(), pos1.getY(), pos1.getZ(), getDirection());
        if (bpt != null) {
          bluePrintBuilder = new BptBuilderBlueprint((Blueprint) bpt, level, pos1.getX(), pos1.getY(), pos1.getZ());
          bptContext = bluePrintBuilder.getContext();
          box.initialize(bluePrintBuilder.xMin(), bluePrintBuilder.yMin(), bluePrintBuilder.zMin(), bluePrintBuilder.xMax(), bluePrintBuilder.yMax(), bluePrintBuilder.zMax());
        }
      } else {
        return;
      }
    }

    Direction direction = getDirection();
    if (laser == null && direction != null && direction != Direction.UP) {
      BlockPos pos2 = getBlockPos();
      laser = new LaserData();
      laser.head = new Position(pos2.getX() + 0.5F, pos2.getY() + 0.5F, pos2.getZ() + 0.5F);
      laser.tail = new Position(pos2.getX() + 0.5F + direction.getStepX() * 0.5F,
        pos2.getY() + 0.5F + direction.getStepY() * 0.5F,
        pos2.getZ() + 0.5F + direction.getStepZ() * 0.5F);
      laser.isVisible = true;
    }

    if (initNBT != null) {
      if (bluePrintBuilder != null) {
        bluePrintBuilder.loadBuildStateToNBT(initNBT.getCompound("builderState"), this);
      }

      initNBT = null;
    }
  }

  @Override
  public void load(@NotNull CompoundTag nbt) {
    super.load(nbt);
    items = NonNullList.withSize(1, ItemStack.EMPTY);
    ContainerHelper.loadAllItems(nbt, items);

    if (nbt.contains("bptBuilder")) {
      initNBT = nbt.getCompound("bptBuilder").copy();
    }
  }

  @Override
  protected void saveAdditional(@NotNull CompoundTag nbt) {
    super.saveAdditional(nbt);
    ContainerHelper.saveAllItems(nbt, items);

    CompoundTag bptNBT = new CompoundTag();
    if (bluePrintBuilder != null) {
      CompoundTag builderCpt = new CompoundTag();
      bluePrintBuilder.saveBuildStateToNBT(builderCpt, this);
      bptNBT.put("builderState", builderCpt);
    }
    nbt.put("bptBuilder", bptNBT);
  }

  @Override
  public List<BuildingItem> getBuilders() {
    return buildersInAction;
  }


  @Override
  public void setRemoved() {
    super.setRemoved();
    if (level != null && !level.isClientSide) {
      currentMarkers.remove(this);
    }
  }

  @Override
  public void onLoad() {
    super.onLoad();
    if (level != null && !level.isClientSide) {
      currentMarkers.add(this);
    }
  }

  public boolean needsToBuild() {
    return !isRemoved() && bluePrintBuilder != null && !bluePrintBuilder.isDone(this);
  }

  public BptContext getContext() {
    return bptContext;
  }

  @Override
  public void addAndLaunchBuildingItem(BuildingItem item) {
    buildersInAction.add(item);
    BCNetworkManager.sendNearLaunchItem(getBlockPos().getCenter(), level.dimension(), getBlockPos(), item);
  }

  @Override
  public Box getBox() {
    return box;
  }

  @Override
  public AABB getRenderBoundingBox() {
    Box renderBox = new Box(this).extendToEncompass(box);

    return renderBox.expand(50).getBoundingBox();
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    box.writeData(data);
    ItemStack itemBlueprint = getBlueprint();
    data.writeByte((laser != null ? 1 : 0) | (!itemBlueprint.isEmpty() ? 2 : 0));
    if (laser != null) {
      laser.writeData(data);
    }
    if (!itemBlueprint.isEmpty()) {
      data.writeItem(itemBlueprint);
    }
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    box.readData(data);
    int flags = data.readUnsignedByte();
    if ((flags & 1) != 0) {
      laser = new LaserData();
      laser.readData(data);
    } else {
      laser = null;
    }
    if ((flags & 2) != 0) {
      setBlueprint(data.readItem());
    } else {
      setBlueprint(ItemStack.EMPTY);
    }
  }

  public void sendBuildersInAction(ServerPlayer player, BlockPos pos) {
    for (BuildingItem i : buildersInAction) {
      BCNetworkManager.sendLaunchItem(player, pos, i);
    }
  }
}
