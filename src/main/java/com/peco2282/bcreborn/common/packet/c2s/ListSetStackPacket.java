/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.core.list.ListNewMenu;
import com.peco2282.bcreborn.core.list.ListOldMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record ListSetStackPacket(
  int lineIndex,
  int slotIndex,
  ItemStack stack
) implements CustomPacket {
  public static ListSetStackPacket decode(FriendlyByteBuf buffer) {
    return new ListSetStackPacket(buffer.readUnsignedByte(), buffer.readUnsignedByte(), buffer.readItem());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeByte(lineIndex);
    buffer.writeByte(slotIndex);
    buffer.writeItem(stack);
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
      else if (ctx.getSender().containerMenu instanceof ListOldMenu menu) {
        menu.setStack(lineIndex, slotIndex, stack);
      }
    });
    ctx.setPacketHandled(true);
  }
}
