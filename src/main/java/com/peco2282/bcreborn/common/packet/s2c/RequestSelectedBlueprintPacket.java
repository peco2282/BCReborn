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

import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_CLIENT)
public record RequestSelectedBlueprintPacket(
  BlockPos pos,
  int blueprintId
) implements CustomPacket {
  public static RequestSelectedBlueprintPacket decode(FriendlyByteBuf buf) {
    return new RequestSelectedBlueprintPacket(buf.readBlockPos(), buf.readInt());
  }

  @Override
  public void encode(FriendlyByteBuf buf) {
    buf.writeBlockPos(pos);
    buf.writeInt(blueprintId);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> context) {
    NetworkEvent.Context ctx = context.get();
    ctx.enqueueWork(() -> getBlockEntity(ctx, pos, BuildersBlockEntityTypes.BLUEPRINT_LIBRARY.get())
      .ifPresent(library -> {
        // TODO: implement when BlueprintDatabase is available
        BCNetworkManager.sendUploadNothing(pos);
      }));
    ctx.setPacketHandled(true);
  }
}
