package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientSetItemInUsePacket(
        int entityId,
        ItemStack itemInUse
) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeItem(itemInUse);
    }

    public static ClientSetItemInUsePacket decode(FriendlyByteBuf buffer) {
        return new ClientSetItemInUsePacket(buffer.readInt(), buffer.readItem());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {

    }
}
