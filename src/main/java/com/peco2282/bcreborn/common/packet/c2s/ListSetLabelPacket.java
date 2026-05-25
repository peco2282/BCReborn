package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.core.list.ListNewMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ListSetLabelPacket(
    String label
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeUtf(label);
  }

  public static ListSetLabelPacket decode(FriendlyByteBuf buffer) {
    return new ListSetLabelPacket(buffer.readUtf());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      if (ctx.getSender().containerMenu instanceof ListNewMenu menu) {
        menu.setLabel(label);
      } else if (ctx.getSender().containerMenu instanceof com.peco2282.bcreborn.core.list.ListOldMenu menu) {
        menu.setLabel(label);
      }
    });
    ctx.setPacketHandled(true);
  }
}
