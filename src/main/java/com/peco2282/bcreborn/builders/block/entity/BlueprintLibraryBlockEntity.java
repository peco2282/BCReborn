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

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.NbtIo;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.api.library.LibraryTypeHandlerByteArray;
import com.peco2282.bcreborn.api.library.LibraryTypeHandlerNBT;
import com.peco2282.bcreborn.builders.blueprints.LibraryDatabase;
import com.peco2282.bcreborn.BCRebornBuilders;
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

/**
 * Port of the original {@code TileBlueprintLibrary} (the Electronic Library). The library itself lives on the
 * player's computer ({@code BCRebornBuilders.getClientDB()}, the {@code blueprints} folder):
 * <ul>
 *   <li>slot 0 → 1: the item's blueprint (or book) is sent to the player who put it in and stored there;</li>
 *   <li>slot 2 → 3: the player's selected library entry is uploaded and written onto the item.</li>
 * </ul>
 * Each takes {@link #PROGRESS_TIME} ticks. The list and the selection are client side; the server only learns
 * the selection index, to know whether there is something to load.
 */
public class BlueprintLibraryBlockEntity extends BuildCraftBlockEntity implements IBlockEntityContainer {
  public static final int CHUNK_SIZE = 16384;
  /** Largest upload the server accepts (the client library has no limit of its own). */
  public static final int MAX_UPLOAD = 16 * 1024 * 1024;
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

  /** Client: re-reads the player's library (original {@code refresh()}). */
  public void refresh() {
    LibraryDatabase db = BCRebornBuilders.getClientDB();
    db.refresh();
    entries = new ArrayList<>(db.getBlueprintIds());
    if (selected >= entries.size()) selected = -1;
  }

  @Override
  public void initialize() {
    super.initialize();
  }

  /** Client: original {@code deleteSelectedBpt()}, deletes the selected entry from the player's library. */
  public void deleteSelectedBpt() {
    if (selected >= 0 && selected < entries.size()) {
      BCRebornBuilders.getClientDB().deleteBlueprint(entries.get(selected));
      selected = -1;
      refresh();
    }
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

  /** Original {@code updateEntity()}: the progress bars run on the server and are synced by the menu. */
  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

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

    // Storing: the item's data goes to the player's library, the item moves on to slot 1.
    if (progressIn >= PROGRESS_TIME && getItem(1).isEmpty()) {
      progressIn = 0;
      storeToPlayer();
    }

    // Loading: ask the player for the selected entry; the answer comes with the upload packets.
    if (progressOut >= PROGRESS_TIME && getItem(3).isEmpty()) {
      progressOut = 0;
      if (downloadingPlayer instanceof ServerPlayer player && !player.hasDisconnected()) {
        BCNetworkManager.sendRequestBlueprint(player, getBlockPos(), selected);
      }
    }
  }

  private void storeToPlayer() {
    LibraryTypeHandler handler = findHandler(0, LibraryTypeHandler.HandlerType.STORE);
    if (handler == null) {
      uploadingPlayer = null;
      return;
    }
    byte[] data = null;
    ItemStack stack = getItem(0);
    if (handler instanceof LibraryTypeHandlerNBT nbtHandler) {
      CompoundTag nbt = new CompoundTag();
      if (nbtHandler.store(stack, nbt)) data = compress(nbt);
    } else if (handler instanceof LibraryTypeHandlerByteArray byteHandler) {
      data = byteHandler.store(stack);
    }
    if (data == null) {
      uploadingPlayer = null;
      return;
    }
    inv.setItem(1, stack);
    inv.setItem(0, ItemStack.EMPTY);
    setChanged();

    LibraryId id = new LibraryId();
    id.name = handler.getName(getItem(1));
    id.extension = handler.getOutputExtension();
    if (uploadingPlayer instanceof ServerPlayer player && !player.hasDisconnected()) {
      BCNetworkManager.sendDownloadBlueprint(player, getBlockPos(), id, data);
    }
    uploadingPlayer = null;
  }

  /**
   * Original {@code uploadServerEnd()}: the uploaded entry is written onto the item in slot 2, which moves to
   * slot 3.
   */
  public void finishUpload() {
    byte[] data = blueprintDownload;
    LibraryId id = blueprintDownloadId;
    completeDownload();
    if (data == null || id == null || !getItem(3).isEmpty()) return;
    LibraryTypeHandler handler = findHandler(2, LibraryTypeHandler.HandlerType.LOAD);
    if (handler == null || !handler.isInputExtension(id.extension)) return;
    ItemStack result = null;
    if (handler instanceof LibraryTypeHandlerNBT nbtHandler) {
      CompoundTag nbt = decompress(data);
      if (nbt != null) {
        if (!nbt.contains("__filename")) nbt.putString("__filename", id.name);
        result = nbtHandler.load(getItem(2).copy(), nbt);
      }
    } else if (handler instanceof LibraryTypeHandlerByteArray byteHandler) {
      result = byteHandler.load(getItem(2).copy(), data);
    }
    if (result != null && !result.isEmpty()) {
      inv.setItem(3, result);
      inv.setItem(2, ItemStack.EMPTY);
      setChanged();
    }
  }

  /** Original {@code uploadNothingToServer()}: nothing was selected, the item passes through unchanged. */
  public void uploadNothing() {
    if (getItem(3).isEmpty()) {
      inv.setItem(3, getItem(2));
      inv.setItem(2, ItemStack.EMPTY);
      setChanged();
    }
    downloadingPlayer = null;
  }

  @Nullable
  public static byte[] compress(CompoundTag nbt) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      NbtIo.writeCompressed(nbt, out);
      return out.toByteArray();
    } catch (IOException e) {
      BCRebornBuilders.LOGGER.error("Failed to write library entry", e);
      return null;
    }
  }

  @Nullable
  public static CompoundTag decompress(byte[] data) {
    try {
      return NbtIo.readCompressed(new ByteArrayInputStream(data));
    } catch (IOException e) {
      BCRebornBuilders.LOGGER.error("Failed to read library entry", e);
      return null;
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
    if (getLevel() == null || getLevel().isClientSide) return null;
    var stack = getItem(slot);
    for (LibraryTypeHandler h : LibraryAPI.getHandlerSet()) {
      if (h.isHandler(stack, type)) {
        return h;
      }
    }
    return null;
  }

  /** Client: original {@code isOutputConsistent()}, whether the selected entry can be written onto slot 2. */
  public boolean isOutputConsistent() {
    if (selected < 0 || selected >= entries.size() || getItem(2).isEmpty()) {
      return false;
    }
    LibraryTypeHandler handler = LibraryAPI.getHandlerFor(entries.get(selected).extension);
    return handler != null && handler.isHandler(getItem(2), LibraryTypeHandler.HandlerType.LOAD);
  }

  public int getSelectedBlueprint() {
    return selected;
  }

  public void setSelectedBlueprint(int blueprintId) {
    selected = blueprintId;
  }

  /** Server: the start of an upload of {@code size} bytes. */
  public void beginUpload(LibraryId id, int size) {
    setBlueprintDownloadAndId(id, new byte[size]);
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
