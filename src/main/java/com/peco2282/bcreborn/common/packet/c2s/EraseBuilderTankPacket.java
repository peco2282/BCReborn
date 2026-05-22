package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record EraseBuilderTankPacket(BlockPos pos) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public static EraseBuilderTankPacket decode(FriendlyByteBuf buffer) {
        return new EraseBuilderTankPacket(buffer.readBlockPos());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BUILDER.get())
                .ifPresent(be -> {
                    // TODO: Implement erase tank logic in BuilderBlockEntity
                    // be.eraseTank();
                });
        });
        ctx.setPacketHandled(true);
    }
}
