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

import com.peco2282.bcreborn.api.tiles.IControllable;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.block.BuilderBlock;
import com.peco2282.bcreborn.builders.menu.BuilderMenu;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.BlockEntityBuffer;
import com.peco2282.bcreborn.common.blueprint.BlueprintBase;
import com.peco2282.bcreborn.common.blueprint.BptBuilderTemplate;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.builder.AbstractBuilderBlockEntity;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.energy.fluids.Tank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BuilderBlockEntity extends AbstractBuilderBlockEntity implements MenuProvider, ILEDProvider {
  private static final int POWER_ACTIVATION = 25;

  private final SimpleInventory inv = new SimpleInventory(28, "Builder", 64);
  private final List<BuildingItem> builders = new LinkedList<>();
  private List<RequirementItemStack> requiredToBuild = new ArrayList<>();
  private CompoundTag initNBT = null;
  private boolean done = true;
  private boolean isBuilding = false;
  private BptBuilderTemplate currentBuilder;

  public BuilderBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(BuildersBlockEntityTypes.BUILDER.get(), p_155229_, p_155230_);
    setBattery(new EnergyStorage(10000, 1000, 1000));
  }

  @Override
  public void initialize() {
    super.initialize();
    cache = BlockEntityBuffer.makeBuffer(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), false);
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      for (BuildingItem b : builders) {
        b.update();
      }
      return;
    }

    if (mode == IControllable.Mode.Off) {
      isBuilding = false;
      return;
    }

    if (getBattery().getEnergyStored() < POWER_ACTIVATION) {
      isBuilding = false;
      return;
    }

    ItemStack blueprintStack = inv.getItem(0);
    if (blueprintStack.isEmpty()) {
      currentBuilder = null;
      isBuilding = false;
      return;
    }

    if (currentBuilder == null) {
      CompoundTag nbt = blueprintStack.getTag();
      if (nbt != null) {
        BlueprintBase bpt = BlueprintBase.loadBluePrint(nbt);
        Direction dir = getBlockState().getValue(BuilderBlock.FACING);
        BlockPos pos1 = getBlockPos();
        bpt = bpt.adjustToWorld(level, pos1.getX(), pos1.getY(), pos1.getZ(), dir);
        currentBuilder = new BptBuilderTemplate(bpt, level, pos1.getX(), pos1.getY(), pos1.getZ());
        done = false;
      }
    }

    if (currentBuilder != null) {
      BlockPos pos2 = getBlockPos();
      isBuilding = currentBuilder.buildNextSlot(level, this, pos2.getX(), pos2.getY(), pos2.getZ());
      if (isBuilding) {
        getBattery().useEnergy(POWER_ACTIVATION, POWER_ACTIVATION, false);
      }
      if (currentBuilder.isDone(this)) {
        done = true;
        currentBuilder = null;
      }
    }

    // Update building items
    List<BuildingItem> toRemove = new ArrayList<>();
    for (BuildingItem b : builders) {
      b.update();
      // TODO: check if building item is finished
    }
    builders.removeAll(toRemove);
  }

  @Override
  public Collection<BuildingItem> getBuilders() {
    return builders;
  }

  @Override
  public void addAndLaunchBuildingItem(BuildingItem item) {
    item.initialize();
    builders.add(item);
  }

  public Container getInventory() {
    return inv;
  }

  @Override
  public List<ItemStack> getInventoryList() {
    List<ItemStack> list = new LinkedList<>();
    for (int i = 1; i < inv.getContainerSize(); i++) {
      list.add(inv.getItem(i));
    }
    return list;
  }

  public SimpleInventory getBuilderInventory() {
    return inv;
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readFromNBT(nbt, "Items");
    done = nbt.getBoolean("done");
    if (nbt.contains("initNBT")) {
      initNBT = nbt.getCompound("initNBT");
    }
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeToNBT(nbt, "Items");
    nbt.putBoolean("done", done);
    if (initNBT != null) {
      nbt.put("initNBT", initNBT);
    }
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    super.writeData(data);
    data.writeBoolean(done);
    data.writeBoolean(isBuilding);
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    super.readData(data);
    done = data.readBoolean();
    isBuilding = data.readBoolean();
  }

  public void updateRequirementsOnGuiOpen(Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      BCNetworkManager.sendSyncBuilderRequirements(serverPlayer, getBlockPos(), requiredToBuild);
    }
  }

  public void addGuiWatcher(Player player) {
    guiWatchers.add(player);
  }

  public void removeGuiWatcher(Player player) {
    guiWatchers.remove(player);
  }

  public List<RequirementItemStack> getRequiredItems() {
    return requiredToBuild;
  }

  public void setItemRequirements(List<RequirementItemStack> requirements) {
    this.requiredToBuild = new ArrayList<>(requirements);
  }

  @Override
  public int getLEDLevel(int led) {
    return isBuilding ? 15 : 0;
  }

  @Override
  public @NotNull Component getDisplayName() {
    return Component.translatable("menu.bcrebornbuilders.builder");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
    return new BuilderMenu(windowId, inventory, this);
  }

  @Override
  public AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new BuilderMenu(p_58627_, p_58628_, this);
  }

  @Override
  public int getContainerSize() {
    return inv.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return inv.isEmpty();
  }

  @Override
  public ItemStack getItem(int p_18942_) {
    return inv.getItem(p_18942_);
  }

  @Override
  public ItemStack removeItem(int p_18942_, int p_18943_) {
    return inv.removeItem(p_18942_, p_18943_);
  }

  @Override
  public ItemStack removeItemNoUpdate(int p_18951_) {
    return inv.removeItemNoUpdate(p_18951_);
  }

  @Override
  public void setItem(int p_18944_, ItemStack p_18945_) {
    inv.setItem(p_18944_, p_18945_);
  }

  @Override
  public boolean stillValid(@NotNull Player player) {
    return super.stillValid(player);
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public @NotNull Component getName() {
    return getDisplayName();
  }

  public void eraseTank(int tankId) {
    List<Tank> tanks = getFluidTanks();
    if (tankId >= 0 && tankId < tanks.size()) {
      tanks.get(tankId).setFluid(null);
      setChanged();
    }
  }

  public List<RequirementItemStack> getNeededItems() {
    return List.copyOf(requiredToBuild);
  }
}
