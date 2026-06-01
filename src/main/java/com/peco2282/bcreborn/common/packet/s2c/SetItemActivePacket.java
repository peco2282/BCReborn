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
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetItemActivePacket(int entityId, boolean active) implements CustomPacket {
  public static SetItemActivePacket decode(FriendlyByteBuf buffer) {
    return new SetItemActivePacket(buffer.readInt(), buffer.readBoolean());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(entityId);
    buffer.writeBoolean(active);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context context = supplier.get();
    context.enqueueWork(() -> {
      RobotEntity robot = (RobotEntity) context.getSender().serverLevel().getEntity(entityId);
      if (robot != null) {
        robot.doItemActivate(active);
      }
    });
    context.setPacketHandled(true);
  }
}
