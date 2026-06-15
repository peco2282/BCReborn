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

import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_CLIENT)
public record InitializePacket(
  int entityId,
  ItemStack itemInUse,
  boolean itemActive
) implements CustomPacket {
  public static InitializePacket decode(FriendlyByteBuf buffer) {
    return new InitializePacket(buffer.readInt(), buffer.readItem(), buffer.readBoolean());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(entityId);
    buffer.writeItem(itemInUse);
    buffer.writeBoolean(itemActive);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      ServerPlayer sender = ctx.getSender();
      if (sender == null) {
        return;
      }

      ServerLevel level = sender.serverLevel();
      Entity entity = level.getEntity(entityId);

      if (entity == null) {
        return;
      }

      // 目的の Entity 型に絞る
      if (entity instanceof RobotEntity robot) {
        robot.itemInUse = itemInUse;
        robot.itemActive = itemActive;
        robot.doInitialize(sender);
      }
    });

    ctx.setPacketHandled(true);
  }
}
