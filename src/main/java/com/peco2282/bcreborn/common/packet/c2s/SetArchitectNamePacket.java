package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetArchitectNamePacket(
    BlockPos pos,
    String name
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeUtf(name);
  }

  public static SetArchitectNamePacket decode(FriendlyByteBuf buffer) {
    return new SetArchitectNamePacket(buffer.readBlockPos(), buffer.readUtf());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();

    ctx.enqueueWork(() ->
        getBlockEntity(ctx, pos, BlockEntityTypesBuilders.ARCHITECT.get())
            .ifPresent(be -> be.setName(name)));
  }
}
