package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.BlockEntityTypesRobotics;
import com.peco2282.bcreborn.robotics.ZonePlan;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestZonePlanSaveAreaPacket(BlockPos pos, int index, ZonePlan plan) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(index);
        plan.writeData(buffer);
    }

    public static RequestZonePlanSaveAreaPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int index = buffer.readUnsignedByte();
        ZonePlan plan = new ZonePlan();
        plan.readData(buffer);
        return new RequestZonePlanSaveAreaPacket(pos, index, plan);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            getBlockEntity(ctx, pos, BlockEntityTypesRobotics.ZONE_PLAN.get())
                .ifPresent(be -> {
                    be.setArea(index, plan);
                });
        });
        ctx.setPacketHandled(true);
    }
}
