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

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.core.list.ListNewMenu;
import com.peco2282.bcreborn.core.list.ListOldMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ListSetLabelPacket(
  String label
) implements CustomPacket {
  public static ListSetLabelPacket decode(FriendlyByteBuf buffer) {
    return new ListSetLabelPacket(buffer.readUtf());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeUtf(label);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      if (ctx.getSender().containerMenu instanceof ListNewMenu menu) {
        menu.setLabel(label);
      } else if (ctx.getSender().containerMenu instanceof ListOldMenu menu) {
        menu.setLabel(label);
      }
    });
    ctx.setPacketHandled(true);
  }
}
