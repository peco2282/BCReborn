/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common.packet.c2s;

import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.BlockEntityTypesRobotics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record RequestRequesterListPacket(BlockPos pos) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    public static RequestRequesterListPacket decode(FriendlyByteBuf buffer) {
        return new RequestRequesterListPacket(buffer.readBlockPos());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            getBlockEntity(ctx, pos, BlockEntityTypesRobotics.REQUESTER.get())
                .ifPresent(be -> {
                    ItemStack[] stacks = new ItemStack[be.getRequestsCount()];
                    for (int i = 0; i < stacks.length; i++) {
                        stacks[i] = be.getRequestTemplate(i);
                    }
                    BCNetworkManager.sendSyncRequesterList(player, pos, stacks);
                });
        });
        ctx.setPacketHandled(true);
    }
}
