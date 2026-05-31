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
package com.peco2282.bcreborn.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public interface CustomPacket {
  void encode(FriendlyByteBuf buffer);

  void handle(Supplier<NetworkEvent.Context> supplier);

  default <BE extends BlockEntity> Optional<BE> getBlockEntity(NetworkEvent.Context ctx, BlockPos pos, BlockEntityType<BE> clazz) {
    Level level;
    if (ctx.getDirection().getReceptionSide().isServer()) {
      level = ctx.getSender().level();
    } else {
      level = Minecraft.getInstance().level;
    }
    if (level == null) return Optional.empty();
    return level.getBlockEntity(pos, clazz);
  }

  default Optional<BlockEntity> getBlockEntity(NetworkEvent.Context ctx, BlockPos pos) {
    Level level;
    if (ctx.getDirection().getReceptionSide().isServer()) {
      level = ctx.getSender().level();
    } else {
      level = Minecraft.getInstance().level;
    }
    if (level == null) return Optional.empty();
    return Optional.ofNullable(level.getBlockEntity(pos));
  }
}
