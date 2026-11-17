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
package com.peco2282.bcreborn.builders.blueprints;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/** The client half of the Electronic Library (original TileBlueprintLibrary client RPCs). */
@OnlyIn(Dist.CLIENT)
public final class BlueprintLibraryClient {

  private BlueprintLibraryClient() {
  }

  @Nullable
  private static BlueprintLibraryBlockEntity library(BlockPos pos) {
    var level = Minecraft.getInstance().level;
    return level != null && level.getBlockEntity(pos) instanceof BlueprintLibraryBlockEntity library ? library : null;
  }

  /** Original {@code downloadBlueprintToClient()}: an item's data arrives and is stored in the player's library. */
  public static void receive(BlockPos pos, LibraryId id, byte[] data) {
    CompoundTag nbt = BlueprintLibraryBlockEntity.decompress(data);
    if (nbt == null) return;
    BCRebornBuilders.getClientDB().add(id, nbt);
    BlueprintLibraryBlockEntity library = library(pos);
    if (library != null) library.refresh();
  }

  /** Original {@code requestSelectedBlueprint()}: uploads the selected entry, or nothing if it does not fit. */
  public static void uploadSelected(BlockPos pos) {
    BlueprintLibraryBlockEntity library = library(pos);
    if (library == null || !library.isOutputConsistent()) {
      BCNetworkManager.sendUploadNothing(pos);
      return;
    }
    LibraryId id = library.entries.get(library.getSelectedBlueprint());
    byte[] data = BlueprintLibraryBlockEntity.compress(BCRebornBuilders.getClientDB().load(id));
    if (data == null || data.length > BlueprintLibraryBlockEntity.MAX_UPLOAD) {
      BCNetworkManager.sendUploadNothing(pos);
      return;
    }
    BCNetworkManager.sendUploadBlueprintBegin(pos, id, data.length);
    int chunk = 0;
    for (int start = 0; start < data.length; start += BlueprintLibraryBlockEntity.CHUNK_SIZE, chunk++) {
      BCNetworkManager.sendUploadBlueprintChunk(pos, chunk,
        Arrays.copyOfRange(data, start, Math.min(data.length, start + BlueprintLibraryBlockEntity.CHUNK_SIZE)));
    }
    BCNetworkManager.sendUploadBlueprintEnd(pos);
  }

  /** Client: selects an entry and tells the server, which only needs to know whether there is a selection. */
  public static void select(BlueprintLibraryBlockEntity library, int index) {
    library.setSelectedBlueprint(index);
    BCNetworkManager.sendSelectBlueprint(library.getBlockPos(), index);
  }
}
