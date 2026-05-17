package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record LaunchItemPacket(
    BlockPos pos,
    BuildingItem item
) implements CustomPacket {
  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    item.writeData(buffer);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {

  }
}
