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
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_CLIENT)
public record LaunchItemPacket(
  BlockPos pos,
  BuildingItem item
) implements CustomPacket {
  public static LaunchItemPacket decode(FriendlyByteBuf buffer) {
    BlockPos pos = buffer.readBlockPos();
    BuildingItem item = new BuildingItem();
    item.readData(buffer);
    return new LaunchItemPacket(pos, item);
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    item.writeData(buffer);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {

  }
}
