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

import com.peco2282.bcreborn.api.library.LibraryAPI;
import com.peco2282.bcreborn.api.library.LibraryTypeHandler;
import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.menu.BlueprintLibraryMenu;
import com.peco2282.bcreborn.common.IBlockEntityContainer;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlueprintLibraryBlockEntity extends BuildCraftBlockEntity implements IBlockEntityContainer {
  public static final int CHUNK_SIZE = 16384;
  private static final int PROGRESS_TIME = 100;
  public SimpleInventory inv = new SimpleInventory(4, "Electronic Library", 1);
  public int progressIn = 0;
  public int progressOut = 0;

  public List<LibraryId> entries = new ArrayList<>();
  public Player uploadingPlayer = null;
  public Player downloadingPlayer = null;
  private int selected = -1;
  private LibraryId blueprintDownloadId;
  private byte[] blueprintDownload;

  public BlueprintLibraryBlockEntity(BlockPos pos, BlockState state) {
    super(BuildersBlockEntityTypes.BLUEPRINT_LIBRARY.get(), pos, state);
  }

  public void refresh() {
    // TODO: implement when BlueprintDatabase is available
  }

  @Override
  public void initialize() {
    super.initialize();
  }

  public void deleteSelectedBpt() {
    // TODO: implement when BlueprintDatabase is available
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readTag(nbt);
    if (nbt.contains("selected")) {
      selected = nbt.getInt("selected");
    }
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeTag(nbt);
    nbt.putInt("selected", selected);
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      if (progressIn > 0 && progressIn < PROGRESS_TIME) {
        progressIn++;
      }

      if (progressOut > 0 && progressOut < PROGRESS_TIME) {
        if (selected != -1) {
          progressOut++;
        } else {
          progressOut = 1;
        }
      }
    }
  }

  @Override
  public Component getName() {
    return getDisplayName();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornbuilders.blueprint_library");
  }

  @Override
  public AbstractContainerMenu createMenu(int windowId, Inventory inventory) {
    return new BlueprintLibraryMenu(windowId, inventory, this);
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
  public ItemStack getItem(int p_18941_) {
    return inv.getItem(p_18941_);
  }

  @Override
  public ItemStack removeItem(int p_18942_, int p_18943_) {
    ItemStack stack = inv.removeItem(p_18942_, p_18943_);
    if (p_18942_ == 0) {
      if (getItem(0).isEmpty()) {
        progressIn = 0;
      }
    }
    if (p_18942_ == 2) {
      if (getItem(2).isEmpty()) {
        progressOut = 0;
      }
    }
    return stack;
  }

  @Override
  public ItemStack removeItemNoUpdate(int p_18951_) {
    return inv.removeItemNoUpdate(p_18951_);
  }

  @Override
  public void setItem(int p_18944_, ItemStack p_18945_) {
    inv.setItem(p_18944_, p_18945_);
    if (p_18944_ == 0) {
      if (!getItem(0).isEmpty() && findHandler(0, LibraryTypeHandler.HandlerType.STORE) != null) {
        progressIn = 1;
      } else {
        progressIn = 0;
      }
    }
    if (p_18944_ == 2) {
      if (!getItem(2).isEmpty() && findHandler(2, LibraryTypeHandler.HandlerType.LOAD) != null) {
        progressOut = 1;
      } else {
        progressOut = 0;
      }
    }
  }

  @Override
  public boolean stillValid(Player p_18946_) {
    var entity = getLevel().getBlockEntity(worldPosition);
    if (entity == null) return false;
    return entity.getClass().equals(getClass());
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Nullable
  private LibraryTypeHandler findHandler(int slot, LibraryTypeHandler.HandlerType type) {
    if (getLevel().isClientSide) return null;
    var stack = getItem(slot);
    for (LibraryTypeHandler h : LibraryAPI.getHandlerSet()) {
      if (h.isHandler(stack, type)) {
        return h;
      }
    }
    return null;
  }

  public boolean isOutputConsistent() {
    return false;
  }

  public int getSelectedBlueprint() {
    return selected;
  }

  public void setSelectedBlueprint(int blueprintId) {
    selected = blueprintId;
  }

  public void setBlueprintDownloadAndId(LibraryId id, byte[] data) {
    blueprintDownloadId = id;
    blueprintDownload = data;
  }

  public void setBlueprintDownload(int start, byte[] data) {
    for (int i = 0; i < data.length; i++) {
      if (start + i >= blueprintDownload.length) {
        return;
      }
      blueprintDownload[start + i] = data[i];
    }
  }

  public byte[] getBlueprintDownload() {
    return blueprintDownload;
  }

  public LibraryId getBlueprintDownloadId() {
    return blueprintDownloadId;
  }

  public void setDownloadingPlayer(Player player) {
    downloadingPlayer = player;
  }

  public void completeDownload() {
    blueprintDownloadId = null;
    blueprintDownload = null;
    downloadingPlayer = null;
  }
}
