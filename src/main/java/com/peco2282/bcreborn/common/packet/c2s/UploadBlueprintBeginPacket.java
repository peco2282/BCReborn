package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record UploadBlueprintBeginPacket(
    BlockPos pos,
    LibraryId libraryId,
    int chunk
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    libraryId.writeData(buffer);
    buffer.writeShort(chunk);
  }

  public static UploadBlueprintBeginPacket decode(FriendlyByteBuf buffer) {
    BlockPos pos = buffer.readBlockPos();
    LibraryId libraryId = LibraryId.decode(buffer);
    short chunk = buffer.readShort();
    return new UploadBlueprintBeginPacket(pos, libraryId, chunk);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BLUEPRINT_LIBRARY.get()).ifPresent(be -> {
        be.setBlueprintDownloadAndId(libraryId, new byte[BlueprintLibraryBlockEntity.CHUNK_SIZE * chunk]);
      });
    });
    ctx.setPacketHandled(true);
  }
}
