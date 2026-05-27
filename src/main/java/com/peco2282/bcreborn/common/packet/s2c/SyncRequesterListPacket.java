package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.menu.RequesterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncRequesterListPacket(BlockPos pos, ItemStack[] requests) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(requests.length);
        for (ItemStack stack : requests) {
            buffer.writeItem(stack);
        }
    }

    public static SyncRequesterListPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int length = buffer.readInt();
        ItemStack[] requests = new ItemStack[length];
        for (int i = 0; i < length; i++) {
            requests[i] = buffer.readItem();
        }
        return new SyncRequesterListPacket(pos, requests);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            AbstractContainerMenu menu = Minecraft.getInstance().player.containerMenu;
            if (menu instanceof RequesterMenu requesterMenu) {
                requesterMenu.requests = requests;
            }
        });
        ctx.setPacketHandled(true);
    }
}
