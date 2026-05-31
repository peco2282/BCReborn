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
package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetSteamDirectionPacket(
  int entityId,
  int x,
  int y,
  int z
) implements CustomPacket {
  public static SetSteamDirectionPacket decode(FriendlyByteBuf buffer) {
    return new SetSteamDirectionPacket(buffer.readInt(), buffer.readInt(), buffer.readShort(), buffer.readInt());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(entityId);
    buffer.writeInt(x);
    buffer.writeShort(y);
    buffer.writeInt(z);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context context = supplier.get();
    if (context.getDirection().getReceptionSide().isServer()) {
      return;
    }

    EntityRobot robot = (EntityRobot) context.getSender().serverLevel().getEntity(entityId);
    if (robot != null) {
      robot.setSteamDirection(x, y, z);
    }
  }
}
