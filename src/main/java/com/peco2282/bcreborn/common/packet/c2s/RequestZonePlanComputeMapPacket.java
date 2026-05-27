package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.BlockEntityTypesRobotics;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestZonePlanComputeMapPacket(BlockPos pos, int cx, int cz, int width, int height, float blocksPerPixel) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(cx);
        buffer.writeInt(cz);
        buffer.writeShort(width);
        buffer.writeShort(height);
        buffer.writeFloat(blocksPerPixel);
    }

    public static RequestZonePlanComputeMapPacket decode(FriendlyByteBuf buffer) {
        return new RequestZonePlanComputeMapPacket(
            buffer.readBlockPos(),
            buffer.readInt(),
            buffer.readInt(),
            buffer.readUnsignedShort(),
            buffer.readUnsignedShort(),
            buffer.readFloat()
        );
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            getBlockEntity(ctx, pos, BlockEntityTypesRobotics.ZONE_PLAN.get())
                .ifPresent(be -> {
                    BCNetworkManager.computeMap(be, cx, cz, width, height, blocksPerPixel, player);
                });
        });
        ctx.setPacketHandled(true);
    }
}
