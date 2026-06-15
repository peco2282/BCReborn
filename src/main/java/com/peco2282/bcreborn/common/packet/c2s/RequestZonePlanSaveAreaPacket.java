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
import com.peco2282.bcreborn.robotics.RoboticsBlockEntityTypes;
import com.peco2282.bcreborn.robotics.zone.ZonePlan;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record RequestZonePlanSaveAreaPacket(BlockPos pos, int index, ZonePlan plan) implements CustomPacket {
  public static RequestZonePlanSaveAreaPacket decode(FriendlyByteBuf buffer) {
    BlockPos pos = buffer.readBlockPos();
    int index = buffer.readUnsignedByte();
    ZonePlan plan = new ZonePlan();
    plan.readData(buffer);
    return new RequestZonePlanSaveAreaPacket(pos, index, plan);
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeByte(index);
    plan.writeData(buffer);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> getBlockEntity(ctx, pos, RoboticsBlockEntityTypes.ZONE_PLAN.get())
      .ifPresent(be -> be.setArea(index, plan)));
    ctx.setPacketHandled(true);
  }
}
