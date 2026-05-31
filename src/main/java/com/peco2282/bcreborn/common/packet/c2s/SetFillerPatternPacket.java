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
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetFillerPatternPacket(BlockPos pos, int delta) implements CustomPacket {
  public static SetFillerPatternPacket decode(FriendlyByteBuf buffer) {
    return new SetFillerPatternPacket(buffer.readBlockPos(), buffer.readInt());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeInt(delta);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      getBlockEntity(ctx, pos, BlockEntityTypesBuilders.FILLER.get())
        .ifPresent(be -> {
          if (delta == 1) {
            be.nextPattern();
          } else if (delta == -1) {
            be.previousPattern();
          } else {
            be.currentPattern = delta;
          }
          be.setChanged();
        });
    });
    ctx.setPacketHandled(true);
  }
}
