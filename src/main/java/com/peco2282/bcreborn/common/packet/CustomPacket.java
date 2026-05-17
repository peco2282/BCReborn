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
    if (ctx.getDirection().getReceptionSide().isServer()) {
      throw new IllegalStateException("Cannot get block entity on server side");
    }

    return ctx.getSender().level().getBlockEntity(pos, clazz);
  }
}
