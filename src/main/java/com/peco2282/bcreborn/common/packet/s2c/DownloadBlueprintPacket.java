package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.blueprint.LibraryId;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record DownloadBlueprintPacket(
    BlockPos pos,
    LibraryId libraryId,
    byte[] data
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    libraryId.generateUniqueId(data);
    libraryId.writeData(buffer);
    buffer.writeByteArray(data);
  }

  public static DownloadBlueprintPacket decode(FriendlyByteBuf buffer) {
    return new DownloadBlueprintPacket(
        buffer.readBlockPos(),
        LibraryId.decode(buffer),
        buffer.readByteArray()
    );
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      // TODO: implement when BlueprintDatabase is available
    });
    ctx.setPacketHandled(true);
  }
}
