package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetSteamDirectionPacket(
        int entityId,
        int x,
        int y,
        int z
) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeInt(x);
        buffer.writeShort(y);
        buffer.writeInt(z);
    }

    public static SetSteamDirectionPacket decode(FriendlyByteBuf buffer) {
        return new SetSteamDirectionPacket(buffer.readInt(), buffer.readInt(), buffer.readShort(), buffer.readInt());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }

        EntityRobot robot = (EntityRobot) context.getSender().serverLevel().getEntity(entityId);
        if (robot != null) {
            robot.setSteamDirection(x, y, z);
        }
    }
}
