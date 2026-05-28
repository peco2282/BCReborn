package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetItemActivePacket(int entityId, boolean active) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeBoolean(active);
    }
    public static SetItemActivePacket decode(FriendlyByteBuf buffer) {
        return new SetItemActivePacket(buffer.readInt(), buffer.readBoolean());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            EntityRobot robot = (EntityRobot) context.getSender().serverLevel().getEntity(entityId);
            if (robot != null) {
                robot.doItemActivate(active);
            }
        });
        context.setPacketHandled(true);
    }
}
