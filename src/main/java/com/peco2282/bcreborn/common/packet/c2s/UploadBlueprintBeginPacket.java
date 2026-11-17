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
package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record UploadBlueprintBeginPacket(
  BlockPos pos,
  LibraryId libraryId,
  int chunk
) implements CustomPacket {
  public static UploadBlueprintBeginPacket decode(FriendlyByteBuf buffer) {
    BlockPos pos = buffer.readBlockPos();
    LibraryId libraryId = LibraryId.decode(buffer);
    int chunk = buffer.readVarInt();
    return new UploadBlueprintBeginPacket(pos, libraryId, chunk);
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    libraryId.writeData(buffer);
    buffer.writeVarInt(chunk);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      // "chunk" is the size of the upload in bytes; bound the allocation before accepting it.
      if (chunk <= 0 || chunk > BlueprintLibraryBlockEntity.MAX_UPLOAD) return;
      getMenuBlockEntity(ctx, pos, BuildersBlockEntityTypes.BLUEPRINT_LIBRARY.get())
        .ifPresent(be -> be.beginUpload(libraryId, chunk));
    });
    ctx.setPacketHandled(true);
  }
}
