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
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.builder.IBuildingItemsProvider;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

/**
 * Original {@code TileAbstractBuilder} {@code "uploadBuildersInAction"} command: a client that just loaded a builder
 * asks for the building items already in flight, and gets one {@code LaunchItemPacket} per item.
 */
@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record RequestBuildingItemsPacket(BlockPos pos) implements CustomPacket {
  /** Requests from further away are ignored (the launch packets only go to players within 64 blocks). */
  private static final double MAX_DISTANCE_SQR = 64 * 64;

  public static RequestBuildingItemsPacket decode(FriendlyByteBuf buffer) {
    return new RequestBuildingItemsPacket(buffer.readBlockPos());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      ServerPlayer player = ctx.getSender();
      if (player == null || player.distanceToSqr(pos.getCenter()) > MAX_DISTANCE_SQR) return;
      getBlockEntity(ctx, pos).ifPresent(be -> {
        if (be instanceof IBuildingItemsProvider provider) {
          for (BuildingItem item : List.copyOf(provider.getBuilders())) {
            BCNetworkManager.sendLaunchItem(player, pos, item);
          }
        }
      });
    });
    ctx.setPacketHandled(true);
  }
}
