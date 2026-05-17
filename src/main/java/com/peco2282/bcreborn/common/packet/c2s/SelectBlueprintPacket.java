package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SelectBlueprintPacket(
    BlockPos pos,
    int blueprintId
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeInt(blueprintId);
  }

  public static SelectBlueprintPacket decode(FriendlyByteBuf buffer) {
    return new SelectBlueprintPacket(buffer.readBlockPos(), buffer.readInt());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BLUEPRINT_LIBRARY.get())
          .ifPresent(be -> {
            be.setSelectedBlueprint(blueprintId);
          });
    });
    ctx.setPacketHandled(true);
  }
}
