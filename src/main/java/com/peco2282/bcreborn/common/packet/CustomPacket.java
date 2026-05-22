package com.peco2282.bcreborn.common.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public interface CustomPacket {
  void encode(FriendlyByteBuf buffer);
  void handle(Supplier<NetworkEvent.Context> supplier);

  default <BE extends BlockEntity> Optional<BE> getBlockEntity(NetworkEvent.Context ctx, BlockPos pos, BlockEntityType<BE> clazz) {
    net.minecraft.world.level.Level level;
    if (ctx.getDirection().getReceptionSide().isServer()) {
        level = ctx.getSender().level();
    } else {
        level = net.minecraft.client.Minecraft.getInstance().level;
    }
    if (level == null) return Optional.empty();
    return level.getBlockEntity(pos, clazz);
  }
}
