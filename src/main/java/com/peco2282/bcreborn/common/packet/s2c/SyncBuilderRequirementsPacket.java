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

import com.peco2282.bcreborn.builders.BuildersBlockEntityTypes;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.packet.CustomPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record SyncBuilderRequirementsPacket(BlockPos pos,
                                            List<RequirementItemStack> requirements) implements CustomPacket {
  public static SyncBuilderRequirementsPacket decode(FriendlyByteBuf buffer) {
    BlockPos pos = buffer.readBlockPos();
    int size = buffer.readInt();
    List<RequirementItemStack> requirements = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      RequirementItemStack req = new RequirementItemStack(null, 0); // Temporary stack will be overwritten
      req.readData(buffer);
      requirements.add(req);
    }
    return new SyncBuilderRequirementsPacket(pos, requirements);
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeBlockPos(pos);
    buffer.writeInt(requirements.size());
    for (RequirementItemStack req : requirements) {
      req.writeData(buffer);
    }
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context ctx = supplier.get();
    ctx.enqueueWork(() -> {
      if (Minecraft.getInstance().level != null) {
        Minecraft.getInstance().level.getBlockEntity(pos, BuildersBlockEntityTypes.BUILDER.get())
          .ifPresent(be -> be.setItemRequirements(requirements));
      }
    });
    ctx.setPacketHandled(true);
  }
}
