package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EraseBuilderTankPacket(BlockPos pos, int tankId) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(tankId);
    }

    public static EraseBuilderTankPacket decode(FriendlyByteBuf buffer) {
        return new EraseBuilderTankPacket(buffer.readBlockPos(), buffer.readInt());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BUILDER.get())
                .ifPresent(be -> be.eraseTank(tankId));
        });
        ctx.setPacketHandled(true);
    }
}
