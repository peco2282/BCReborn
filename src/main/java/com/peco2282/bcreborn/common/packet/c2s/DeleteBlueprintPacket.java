package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record DeleteBlueprintPacket(BlockPos pos) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public static DeleteBlueprintPacket decode(FriendlyByteBuf buffer) {
        return new DeleteBlueprintPacket(buffer.readBlockPos());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            getBlockEntity(ctx, pos, BlockEntityTypesBuilders.BLUEPRINT_LIBRARY.get())
                .ifPresent(be -> {
                    be.deleteSelectedBpt();
                });
        });
        ctx.setPacketHandled(true);
    }
}
