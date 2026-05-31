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

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.blueprint.BlueprintReadConfiguration;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetReadArchitectConfigurationPacket(
  BlockPos pos,
  BlueprintReadConfiguration config
) implements CustomPacket {
  public static SetReadArchitectConfigurationPacket decode(FriendlyByteBuf buffer) {
    BlockPos pos = buffer.readBlockPos();
    BlueprintReadConfiguration config = new BlueprintReadConfiguration();
    config.readData(buffer);
    return new SetReadArchitectConfigurationPacket(pos, config);
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    config.writeData(buffer);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> getBlockEntity(ctx, pos, BlockEntityTypesBuilders.ARCHITECT.get())
      .ifPresent(be -> {
        be.setReadConfiguration(config);
        be.getUpdatePacket();
      }));
    ctx.setPacketHandled(true);
  }
}
