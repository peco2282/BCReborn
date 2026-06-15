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

import com.peco2282.bcreborn.builders.block.entity.BlueprintLibraryBlockEntity;
import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record UploadNothingToServerPacket(BlockPos pos) implements CustomPacket {
  public static UploadNothingToServerPacket decode(FriendlyByteBuf buffer) {
    return new UploadNothingToServerPacket(buffer.readBlockPos());
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();

    ctx.enqueueWork(() -> {
      BlockEntity entity = ctx.getSender().level().getBlockEntity(pos);
      if (!(entity instanceof BlueprintLibraryBlockEntity library)) return;

      library.setItem(3, library.getItem(2));
      library.setItem(4, ItemStack.EMPTY);
    });

    ctx.setPacketHandled(true);
  }
}
