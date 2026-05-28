package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientSetInventoryPacket(int entityId, short slot, ItemStack stack) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeShort(slot);
        buffer.writeItem(stack);
    }

    public static ClientSetInventoryPacket decode(FriendlyByteBuf buffer) {
        return new ClientSetInventoryPacket(buffer.readInt(), buffer.readShort(), buffer.readItem());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            EntityRobot robot = (EntityRobot) supplier.get().getSender().serverLevel().getEntity(entityId);
            if (robot != null) {
                robot.clientSetInventory(slot, stack);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
