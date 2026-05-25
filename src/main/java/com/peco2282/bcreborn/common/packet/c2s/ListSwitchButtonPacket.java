package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.core.list.ListNewMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ListSwitchButtonPacket(
    int lineIndex,
    int button
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeByte(lineIndex);
    buffer.writeByte(button);
  }

  public static ListSwitchButtonPacket decode(FriendlyByteBuf buffer) {
    return new ListSwitchButtonPacket(buffer.readUnsignedByte(), buffer.readUnsignedByte());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      if (ctx.getSender().containerMenu instanceof ListNewMenu menu) {
        menu.switchButton(lineIndex, button);
      } else if (ctx.getSender().containerMenu instanceof com.peco2282.bcreborn.core.list.ListOldMenu menu) {
        menu.switchButton(lineIndex, button);
      }
    });
    ctx.setPacketHandled(true);
  }
}
