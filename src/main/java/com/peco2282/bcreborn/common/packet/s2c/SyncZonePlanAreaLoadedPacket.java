package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.ZonePlan;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncZonePlanAreaLoadedPacket(BlockPos pos, ZonePlan plan) implements CustomPacket {
    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        plan.writeData(buffer);
    }

    public static SyncZonePlanAreaLoadedPacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        ZonePlan plan = new ZonePlan();
        plan.readData(buffer);
        return new SyncZonePlanAreaLoadedPacket(pos, plan);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            AbstractContainerMenu menu = Minecraft.getInstance().player.containerMenu;
            if (menu instanceof ZonePlanMenu zonePlanMenu) {
                zonePlanMenu.currentAreaSelection = plan;
                if (zonePlanMenu.gui != null) {
                    zonePlanMenu.gui.refreshSelectedArea();
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
