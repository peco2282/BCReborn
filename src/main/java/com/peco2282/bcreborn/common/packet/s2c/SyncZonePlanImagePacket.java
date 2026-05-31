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
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncZonePlanImagePacket(BlockPos pos, int totalSize, int offset, byte[] data) implements CustomPacket {
  public static SyncZonePlanImagePacket decode(FriendlyByteBuf buffer) {
    return new SyncZonePlanImagePacket(
      buffer.readBlockPos(),
      buffer.readInt(),
      buffer.readInt(),
      buffer.readByteArray()
    );
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeInt(totalSize);
    buffer.writeInt(offset);
    buffer.writeByteArray(data);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      AbstractContainerMenu menu = Minecraft.getInstance().player.containerMenu;
      if (menu instanceof ZonePlanMenu zonePlanMenu && zonePlanMenu.gui != null) {
        zonePlanMenu.gui.applyMapImageBytes(offset, data);
        // TODO: use dynamic texture from screen
                /*
                for (int i = 0; i < data.length; ++i) {
                    if (offset + i < zonePlanMenu.gui.mapTexture.colorMap.length) {
                        zonePlanMenu.gui.mapTexture.colorMap[offset + i] = 0xFF000000 | MapColor.byId(data[i] & 0xFF).getCol();
                    }
                }
                */
      }
    });
    ctx.setPacketHandled(true);
  }
}
