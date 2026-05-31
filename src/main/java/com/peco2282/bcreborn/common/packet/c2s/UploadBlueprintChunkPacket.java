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

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;
import java.util.function.Supplier;

public record UploadBlueprintChunkPacket(
  BlockPos pos,
  int chunk,
  byte[] data
) implements CustomPacket {
  public UploadBlueprintChunkPacket(BlockPos pos, int chunk, byte[] data, int start) {
    this(pos, chunk, Arrays.copyOfRange(data, start, start + data.length));
  }

  public static UploadBlueprintChunkPacket decode(FriendlyByteBuf buffer) {
    return new UploadBlueprintChunkPacket(
      buffer.readBlockPos(),
      buffer.readInt(),
      buffer.readByteArray()
    );
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeInt(chunk);
    buffer.writeByteArray(data);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BLUEPRINT_LIBRARY.get()).ifPresent(be -> {
      int start = chunk * BlueprintLibraryBlockEntity.CHUNK_SIZE;
      if (be.getBlueprintDownload() == null) {
        be.setBlueprintDownload(start, data);
      }
    }));
    ctx.setPacketHandled(true);
  }
}
