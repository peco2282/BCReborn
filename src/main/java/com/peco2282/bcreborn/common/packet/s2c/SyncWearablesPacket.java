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
package com.peco2282.bcreborn.common.packet.s2c;

import com.peco2282.bcreborn.common.bean.Packet;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import com.peco2282.bcreborn.robotics.entity.RobotEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Packet(direction = NetworkDirection.PLAY_TO_CLIENT)
public record SyncWearablesPacket(
  int entityId,
  List<ItemStack> wearables
) implements CustomPacket {
  public static SyncWearablesPacket decode(FriendlyByteBuf buffer) {
    int entityId = buffer.readInt();
    int size = buffer.readInt();
    List<ItemStack> wearables = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      wearables.add(buffer.readItem());
    }
    return new SyncWearablesPacket(entityId, wearables);
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(entityId);
    buffer.writeInt(wearables.size());
    for (ItemStack stack : wearables) {
      buffer.writeItem(stack);
    }
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context context = supplier.get();
    context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient));
    context.setPacketHandled(true);
  }

  @OnlyIn(Dist.CLIENT)
  private void handleClient() {
    if (Minecraft.getInstance().level == null) {
      return;
    }
    Entity entity = Minecraft.getInstance().level.getEntity(entityId);
    if (entity instanceof RobotEntity robot) {
      robot.doSyncWearables(wearables);
    }
  }
}
