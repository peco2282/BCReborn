package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record ClearItemRequirementsPacket(
    BlockPos pos
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
  }

  public static ClearItemRequirementsPacket decode(FriendlyByteBuf buffer) {
    return new ClearItemRequirementsPacket(buffer.readBlockPos());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BUILDER.get()).ifPresent(be -> {
        be.setItemRequirements(List.of());
      });
    });
    ctx.setPacketHandled(true);
  }
}
