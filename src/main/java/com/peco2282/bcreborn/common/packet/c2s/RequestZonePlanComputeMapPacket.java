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

import com.peco2282.bcreborn.robotics.map.MapWorld;
import com.peco2282.bcreborn.robotics.map.MapManager;
import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.RoboticsBlockEntityTypes;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_SERVER)
public record RequestZonePlanComputeMapPacket(BlockPos pos, int cx, int cz, int width, int height,
                                              float blocksPerPixel) implements CustomPacket {
  public static RequestZonePlanComputeMapPacket decode(FriendlyByteBuf buffer) {
    return new RequestZonePlanComputeMapPacket(
      buffer.readBlockPos(),
      buffer.readInt(),
      buffer.readInt(),
      buffer.readUnsignedShort(),
      buffer.readUnsignedShort(),
      buffer.readFloat()
    );
  }

  /** Largest map a client may ask for, and the zoom range of the Zone Planner GUI. */
  public static final int MAX_SIZE = 512;
  public static final float MIN_BLOCKS_PER_PIXEL = 0.125f;
  public static final float MAX_BLOCKS_PER_PIXEL = 16.0f;

  /**
   * Original {@code ContainerZonePlan.computeMap()}: the map colour of every pixel's block column, inside the area
   * the planner maps; black outside it and where nothing is mapped yet.
   */
  public static byte[] computeMap(ZonePlanBlockEntity be, int cx, int cz, int width, int height, float blocksPerPixel) {
    final byte[] textureData = new byte[width * height];
    int startX = Math.round(cx - width * blocksPerPixel / 2);
    int startZ = Math.round(cz - height * blocksPerPixel / 2);
    int endX = Math.round(startX + (width - 1) * blocksPerPixel);
    int endZ = Math.round(startZ + (height - 1) * blocksPerPixel);
    MapWorld world = MapManager.getLoaded(be.getLevel(), startX >> 4, startZ >> 4, endX >> 4, endZ >> 4);
    if (world == null) return textureData;
    for (int i = 0; i < width; ++i) {
      for (int j = 0; j < height; ++j) {
        int x = Math.round(startX + i * blocksPerPixel);
        int z = Math.round(startZ + j * blocksPerPixel);
        if (be.isInMapArea(x, z) && world.hasChunk(x >> 4, z >> 4)) {
          textureData[i + j * width] = (byte) world.getColor(x, z);
        }
      }
    }
    return textureData;
  }

  private static void sendMap(ZonePlanBlockEntity be, int cx, int cz, int width, int height, float blocksPerPixel, ServerPlayer player) {
    if (width <= 0 || height <= 0 || width > MAX_SIZE || height > MAX_SIZE
      || !(blocksPerPixel >= MIN_BLOCKS_PER_PIXEL && blocksPerPixel <= MAX_BLOCKS_PER_PIXEL)) return;
    final byte[] textureData = computeMap(be, cx, cz, width, height, blocksPerPixel);
    int MAX_PACKET_LENGTH = 30000;
    for (int i = 0; i < textureData.length; i += MAX_PACKET_LENGTH) {
      int len = Math.min(textureData.length - i, MAX_PACKET_LENGTH);
      byte[] chunk = new byte[len];
      System.arraycopy(textureData, i, chunk, 0, len);
      BCNetworkManager.sendSyncZonePlanImage(player, be.getBlockPos(), textureData.length, i, chunk);
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeInt(cx);
    buffer.writeInt(cz);
    buffer.writeShort(width);
    buffer.writeShort(height);
    buffer.writeFloat(blocksPerPixel);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      ServerPlayer player = ctx.getSender();
      if (player == null) return;
      getMenuBlockEntity(ctx, pos, RoboticsBlockEntityTypes.ZONE_PLAN.get())
        .ifPresent(be -> sendMap(be, cx, cz, width, height, blocksPerPixel, player));
    });
    ctx.setPacketHandled(true);
  }
}
