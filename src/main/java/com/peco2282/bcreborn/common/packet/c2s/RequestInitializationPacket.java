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

import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestInitializationPacket(
  int entityId,
  ItemStack itemInUse,
  boolean itemActive
) implements CustomPacket {
  public static RequestInitializationPacket decode(FriendlyByteBuf buffer) {
    return new RequestInitializationPacket(buffer.readInt(), buffer.readItem(), buffer.readBoolean());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(entityId);
    buffer.writeItem(itemInUse);
    buffer.writeBoolean(itemActive);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> BCNetworkManager.sendInitialize(supplier.get().getSender(), entityId, itemInUse, itemActive));
    supplier.get().setPacketHandled(true);
  }
}
