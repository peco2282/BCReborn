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
import com.peco2282.bcreborn.robotics.RoboticsBlockEntityTypes;
import com.peco2282.bcreborn.robotics.zone.ZonePlan;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestZonePlanLoadAreaPacket(BlockPos pos, int index) implements CustomPacket {
  public static RequestZonePlanLoadAreaPacket decode(FriendlyByteBuf buffer) {
    return new RequestZonePlanLoadAreaPacket(buffer.readBlockPos(), buffer.readUnsignedByte());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeByte(index);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      ServerPlayer player = ctx.getSender();
      if (player == null) return;
      getBlockEntity(ctx, pos, RoboticsBlockEntityTypes.ZONE_PLAN.get())
        .ifPresent(be -> {
          ZonePlan plan = be.selectArea(index);
          BCNetworkManager.sendSyncZonePlanAreaLoaded(player, pos, plan);
        });
    });
    ctx.setPacketHandled(true);
  }
}
