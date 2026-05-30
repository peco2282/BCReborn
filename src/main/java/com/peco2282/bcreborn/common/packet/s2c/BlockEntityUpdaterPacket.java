package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BlockEntityUpdaterPacket(
    BlockPos pos,
    byte[] data
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeByteArray(data);
  }

  public static BlockEntityUpdaterPacket decode(FriendlyByteBuf buffer) {
    return new BlockEntityUpdaterPacket(buffer.readBlockPos(), buffer.readByteArray());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos).ifPresent(be -> {
        if (be instanceof BuildCraftBlockEntity bcbe) {
          FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(data));
          bcbe.readData(buf);
        }
      });
    });
    ctx.setPacketHandled(true);
  }
}
