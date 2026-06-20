package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.RoboticsBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record RequestZonePlanSetNamePacket(
  BlockPos pos,
  String name
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeUtf(name);
  }

  public static RequestZonePlanSetNamePacket decode(FriendlyByteBuf buffer) {
    return new RequestZonePlanSetNamePacket(buffer.readBlockPos(), buffer.readUtf());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos, RoboticsBlockEntityTypes.ZONE_PLAN.get()).ifPresent(be -> {
        be.doSetName(name);
      });
    });
    ctx.setPacketHandled(true);
  }
}
