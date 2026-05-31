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
package com.peco2282.bcreborn.common.packet;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PacketGuiWidget(int windowId, int widgetId, byte[] data) implements CustomPacket {
  public static PacketGuiWidget decode(FriendlyByteBuf buffer) {
    return new PacketGuiWidget(buffer.readInt(), buffer.readInt(), buffer.readByteArray());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(windowId);
    buffer.writeInt(widgetId);
    buffer.writeByteArray(data);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    // This is handled by BuildCraftMenu usually, but let's implement basic handling here if needed
    // In 1.20.1, we often handle this in the menu itself via a custom message
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.containerMenu.containerId == windowId) {
        if (player.containerMenu instanceof BuildCraftMenu<?> menu) {
          menu.handleWidgetClientData(widgetId, new FriendlyByteBuf(Unpooled.wrappedBuffer(data)));
        }
      }
    });
    ctx.setPacketHandled(true);
  }
}
