package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record SyncWearablesPacket(
        int entityId,
        List<ItemStack> wearables
) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeInt(wearables.size());
        for (ItemStack stack : wearables) {
            buffer.writeItem(stack);
        }
    }

    public static SyncWearablesPacket decode(FriendlyByteBuf buffer) {
        return new SyncWearablesPacket(buffer.readInt(), buffer.readList(FriendlyByteBuf::readItem));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            EntityRobot robot = (EntityRobot) supplier.get().getSender().serverLevel().getEntity(entityId);
            if (robot != null) {
                robot.doSyncWearables(wearables);
            }
        });
        supplier.get().setPacketHandled(true);
    }
}
