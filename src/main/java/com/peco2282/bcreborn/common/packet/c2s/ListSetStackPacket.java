package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.core.list.ListNewMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ListSetStackPacket(
    int lineIndex,
    int slotIndex,
    ItemStack stack
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeByte(lineIndex);
    buffer.writeByte(slotIndex);
    buffer.writeItem(stack);
  }

  public static ListSetStackPacket decode(FriendlyByteBuf buffer) {
    return new ListSetStackPacket(buffer.readUnsignedByte(), buffer.readUnsignedByte(), buffer.readItem());
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      if (ctx.getSender().containerMenu instanceof ListNewMenu menu) {
        menu.setStack(lineIndex, slotIndex, stack);
      }
      // ListOldMenu の場合も同様に処理する必要があるかもしれない
      // しかし receiveCommand のロジックを見ると同じ setStack メソッドを持っている
      else if (ctx.getSender().containerMenu instanceof com.peco2282.bcreborn.core.list.ListOldMenu menu) {
        menu.setStack(lineIndex, slotIndex, stack);
      }
    });
    ctx.setPacketHandled(true);
  }
}
